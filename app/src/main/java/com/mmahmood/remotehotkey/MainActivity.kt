package com.mmahmood.remotehotkey

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.*
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseData
import android.bluetooth.le.AdvertiseSettings
import android.bluetooth.le.BluetoothLeAdvertiser
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.ParcelUuid
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.mmahmood.remotehotkey.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.custom_tile.view.*
import java.util.*

// Added Lint because permission check is redundant.
@SuppressLint("MissingPermission")
class MainActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    private var mFirstRun = true
    private var mRows = 6
    private var mCols = 8

    private lateinit var binding: ActivityMainBinding
    private lateinit var mSpinner: Spinner
    // Bluetooth API
    private lateinit var bluetoothManager: BluetoothManager
    private var bluetoothGattServer: BluetoothGattServer? = null
    // Collection of notification subscribers:
    private val registeredDevices = mutableSetOf<BluetoothDevice>()
    private var mGattActive = false
    // If you want to limit number of registered devices:
//    private var registeredDeviceLimit = 1
    private var permissionsSet = false

    private val allTileSets = arrayOf(AppConstant.DefaultTileSet, AppConstant.TextTileSet,
        AppConstant.AltiumTileSet)
    private var currentTileset = 0

    // Callbacks for BluetoothLE:
    /**
     * Listens for Bluetooth adapter events to enable/disable
     * advertising and server functionality.
     */
    private val bluetoothReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.STATE_OFF)) {
                BluetoothAdapter.STATE_ON -> {
                    startAdvertising()
                    startServer()
                }
                BluetoothAdapter.STATE_OFF -> {
                    stopServer()
                    stopAdvertising()
                }
            }
        }
    }


    /**
     * Callback to receive information about the advertisement process.
     */
    private val advertiseCallback = object : AdvertiseCallback() {
        override fun onStartSuccess(settingsInEffect: AdvertiseSettings) {
            Log.i(TAG, "LE Advertise Started.")
            mGattActive = true
        }

        override fun onStartFailure(errorCode: Int) {
            Log.w(TAG, "LE Advertise Failed: $errorCode")
        }
    }

    /**
     * Callback to handle incoming requests to the GATT server.
     * All read/write requests for characteristics and descriptors are handled here.
     */
    private val gattServerCallback = object : BluetoothGattServerCallback() {

        override fun onConnectionStateChange(device: BluetoothDevice, status: Int, newState: Int) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.i(TAG, "BluetoothDevice CONNECTED: $device")
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.i(TAG, "BluetoothDevice DISCONNECTED: $device")
                //Remove device from any active subscriptions
                registeredDevices.remove(device)
            }
        }

        override fun onCharacteristicReadRequest(device: BluetoothDevice, requestId: Int, offset: Int,
                                                 characteristic: BluetoothGattCharacteristic
        ) {
            when (characteristic.uuid) {
                RemoteHotkeyService.REMOTE_HOTKEY_CHAR -> {
                    Log.i(TAG, "[RemoteHotkeyService] Read Remote Char")
                    bluetoothGattServer?.sendResponse(
                        device,
                        requestId,
                        BluetoothGatt.GATT_SUCCESS,
                        0,
                        byteArrayOf(0x81.toByte(),0xF1.toByte())
                    )
                }
                else -> {
                    // Invalid characteristic
                    Log.w(TAG, "Invalid Characteristic Read: " + characteristic.uuid)
                    bluetoothGattServer?.sendResponse(
                        device,
                        requestId,
                        BluetoothGatt.GATT_FAILURE,
                        0,
                        null
                    )
                }
            }
        }

        override fun onDescriptorReadRequest(device: BluetoothDevice, requestId: Int, offset: Int,
                                             descriptor: BluetoothGattDescriptor
        ) {
            if (RemoteHotkeyService.CLIENT_CONFIG == descriptor.uuid) {
                Log.d(TAG, "Config descriptor read")
                val returnValue = if (registeredDevices.contains(device)) {
                    BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                } else {
                    BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE
                }
                bluetoothGattServer?.sendResponse(device,
                    requestId,
                    BluetoothGatt.GATT_SUCCESS,
                    0,
                    returnValue)
            } else {
                Log.w(TAG, "Unknown descriptor read request")
                bluetoothGattServer?.sendResponse(device,
                    requestId,
                    BluetoothGatt.GATT_FAILURE,
                    0, null)
            }
        }

        override fun onDescriptorWriteRequest(device: BluetoothDevice, requestId: Int,
                                              descriptor: BluetoothGattDescriptor,
                                              preparedWrite: Boolean, responseNeeded: Boolean,
                                              offset: Int, value: ByteArray) {
            if (RemoteHotkeyService.CLIENT_CONFIG == descriptor.uuid) {
                if (Arrays.equals(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE, value)) {
                    Log.d(TAG, "Subscribe device to notifications: $device")
                    registeredDevices.add(device)
                } else if (Arrays.equals(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE, value)) {
                    Log.d(TAG, "Unsubscribe device from notifications: $device")
                    registeredDevices.remove(device)
                }

                if (responseNeeded) {
                    bluetoothGattServer?.sendResponse(device,
                        requestId,
                        BluetoothGatt.GATT_SUCCESS,
                        0, null)
                }
            } else {
                Log.w(TAG, "Unknown descriptor write request")
                if (responseNeeded) {
                    bluetoothGattServer?.sendResponse(device,
                        requestId,
                        BluetoothGatt.GATT_FAILURE,
                        0, null)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Keep screen on:
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        // TODO: Temporarily locking to landscape orientation (see this.requestedOrientation)
        this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        // Init Spinner:
        mSpinner = binding.multimodeSpinner
        mSpinner.onItemSelectedListener = this
        ArrayAdapter.createFromResource(
            this,
            R.array.mmode_spinner_menu,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            mSpinner.adapter = adapter
        }
        // Init Default Tiles:

        // Initialize Bluetooth:
        bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        if (permissionsSet) initGattServer()
    }

    private fun removeAllTiles() {

    }

    private fun initializeTileSet(dataReference: Array<HotkeyData>) {
        // Code adapted from - https://stackoverflow.com/questions/51430129/create-grid-n-%C3%97-n-in-android-constraintlayout-with-variable-number-of-n
        val layout = binding.conlayout
        var customTileView: CustomTileView
        var lp: ConstraintLayout.LayoutParams
        var id: Int
        val idArray = Array(mRows) { IntArray(mCols) }
        val cs = ConstraintSet()

        // TODO: Replace TextView with ImageViews or other custom view
        // Pull characteristics from settings/saved data
        // Add our views to the ConstraintLayout.

        for (iRow in 0 until mRows) {
            for (iCol in 0 until mCols) {
                lp = ConstraintLayout.LayoutParams(
                    ConstraintSet.MATCH_CONSTRAINT,
                    ConstraintSet.MATCH_CONSTRAINT
                )
                if (mFirstRun) {
                    id = View.generateViewId()
                    mFirstRun = false
                } else {
                    id = (8*iRow) + 1 + iCol
                }
                val data = if ((id-1) > (dataReference.size - 1)) {
                    AppConstant.DefaultTile
                } else {
                    dataReference[id-1]
                }
                Log.e(TAG, "Current Id: #$id: name: ${data.name}, path: ${data.drawablePath}")
                val dataString = data.name.ifEmpty { id.toString() }
                val drawablePath = data.drawablePath.ifEmpty { "placeholder" }
                idArray[iRow][iCol] = id
                customTileView = CustomTileView(this)
                customTileView.id = id
                customTileView.setTitle(dataString)
                customTileView.titleTextView.textSize = 16f
                customTileView.titleTextView.setTextColor(Color.RED)
                customTileView.setBackgroundColor(Color.WHITE)
                val drawableId = applicationContext.resources.getIdentifier(drawablePath, "drawable", packageName)
                customTileView.imageView.setImageResource(drawableId)
                layout.addView(customTileView, lp)
            }
        }

        // Create horizontal chain for each row and set the 1:1 dimensions.
        // but first make sure the layout frame has the right ratio set.

        // Create horizontal chain for each row and set the 1:1 dimensions.
        // but first make sure the layout frame has the right ratio set.
        cs.clone(layout)
        cs.setDimensionRatio(binding.gridFrame.id, "$mCols:$mRows")
        for (iRow in 0 until mRows) {
            for (iCol in 0 until mCols) {
                id = idArray[iRow][iCol]
                cs.setDimensionRatio(id, "1:1")
                if (iRow == 0) {
                    // Connect the top row to the top of the frame.
                    cs.connect(id, ConstraintSet.TOP, binding.gridFrame.id, ConstraintSet.TOP)
                } else {
                    // Connect top to bottom of row above.
                    cs.connect(id, ConstraintSet.TOP, idArray[iRow - 1][0], ConstraintSet.BOTTOM)
                }
            }
            // Create a horiontal chain that will determine the dimensions of our squares.
            // Could also be createHorizontalChainRtl() with START/END.
            cs.createHorizontalChain(
                binding.gridFrame.id, ConstraintSet.LEFT,
                binding.gridFrame.id, ConstraintSet.RIGHT,
                idArray[iRow], null, ConstraintSet.CHAIN_PACKED
            )
        }

        // Make views clickable
        for (iRow in 0 until mRows) {
            for (iCol in 0 until mCols) {
                findViewById<CustomTileView>(idArray[iRow][iCol]).setOnClickListener {
                    sendCommand(idArray[iRow][iCol], iRow, iCol)
                }
            }
        }

        cs.applyTo(layout)
    }

    private fun initGattServer() {
        if (!mGattActive) {
            val bluetoothAdapter = bluetoothManager.adapter

            // Check for bluetooth support, else exit:
            if (!checkBluetoothSupport(bluetoothAdapter)) {
                finish()
            }

            // Register for system Bluetooth events:
            val filter = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
            registerReceiver(bluetoothReceiver, filter)
            if (!bluetoothAdapter.isEnabled) {
                Log.d(TAG, "Bluetooth is currently disabled...enabling")
                bluetoothAdapter.enable()
            } else {
                Log.d(TAG, "Bluetooth enabled...starting services")
                startAdvertising()
                startServer()
            }
        }
    }

    /**
     * Begin advertising over Bluetooth that this device is connectable
     * and supports the Service.
     */
    private fun startAdvertising() {
        val bluetoothLeAdvertiser: BluetoothLeAdvertiser? =
            bluetoothManager.adapter.bluetoothLeAdvertiser

        bluetoothLeAdvertiser?.let {
            val settings = AdvertiseSettings.Builder()
                .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_BALANCED)
                .setConnectable(true)
                .setTimeout(0)
                .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM)
                .build()

            val data = AdvertiseData.Builder()
                .setIncludeDeviceName(true)
                .setIncludeTxPowerLevel(false)
                .addServiceUuid(ParcelUuid(RemoteHotkeyService.REMOTE_HOTKEY_SERVICE))
                .build()
            it.startAdvertising(settings, data, advertiseCallback)
        } ?: Log.w(TAG, "Failed to create advertiser")
    }

    /**
     * Stop Bluetooth advertisements.
     */
    private fun stopAdvertising() {
        val bluetoothLeAdvertiser: BluetoothLeAdvertiser? =
            bluetoothManager.adapter.bluetoothLeAdvertiser
        bluetoothLeAdvertiser?.stopAdvertising(advertiseCallback) ?: Log.w(TAG, "Failed to create advertiser")
    }

    /**
     * Initialize the GATT server instance with the services/characteristics
     * from the Time Profile.
     */
    private fun startServer() {
        bluetoothGattServer = bluetoothManager.openGattServer(this, gattServerCallback)

        bluetoothGattServer?.addService(RemoteHotkeyService.createHotkeyService())
            ?: Log.w(TAG, "Unable to create GATT server")
    }

    /**
     * Shut down the GATT server.
     */
    private fun stopServer() {
        bluetoothGattServer?.close()
    }


    private fun sendCommand(id: Int, row: Int, col: Int) {
        Log.e(TAG, "Request Sent for [R$row, C$col], id=$id.")
        sendBluetoothCommandUpdate(allTileSets[currentTileset][id-1].byteArray)
        Toast.makeText(applicationContext, "Sending Command for ${allTileSets[currentTileset][id-1].name}", Toast.LENGTH_SHORT).show()
    }

    private fun sendBluetoothCommandUpdate(data: ByteArray) {
        if (registeredDevices.isEmpty()) {
            Log.i(TAG, "No devices/subscribers registered")
            return
        }
        Log.i(TAG, "Sending update to ${registeredDevices.size} subscribers")
        for (d in registeredDevices) {
            val char = bluetoothGattServer?.getService(RemoteHotkeyService.REMOTE_HOTKEY_SERVICE)
                ?.getCharacteristic(RemoteHotkeyService.REMOTE_HOTKEY_CHAR)
            char?.value = data
            bluetoothGattServer?.notifyCharacteristicChanged(d, char, false)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mGattActive) {
            val bluetoothAdapter = bluetoothManager.adapter
            if (bluetoothAdapter.isEnabled) {
                stopServer()
                stopAdvertising()
            }

            unregisterReceiver(bluetoothReceiver)
        }
    }

    private fun checkPermissions(): Boolean {
        val deniedPermissions = PERMISSIONS_LIST.filter { ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED }
        if (deniedPermissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, deniedPermissions.toTypedArray(), MULTIPLE_PERMISSIONS_REQUEST)
            return false
        }
        return true
    }

    override fun onResume() {
        super.onResume()
        if (!permissionsSet) {
            permissionsSet = checkPermissions()
            initGattServer()
            permissionsSet = true
        }
    }

    /**
     * Verify the level of Bluetooth support provided by the hardware.
     * @param bluetoothAdapter System [BluetoothAdapter].
     * @return true if Bluetooth is properly supported, false otherwise.
     */
    private fun checkBluetoothSupport(bluetoothAdapter: BluetoothAdapter?): Boolean {

        if (bluetoothAdapter == null) {
            Log.w(TAG, "Bluetooth is not supported")
            return false
        }

        if (!packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Log.w(TAG, "Bluetooth LE is not supported")
            return false
        }

        return true
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        Log.i(TAG, "Spinner: Nothing selected")
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        currentTileset = position
        if (position >= allTileSets.size) {
            Toast.makeText(applicationContext, "List Nonexistent!" , Toast.LENGTH_SHORT).show()
            return
        }
        initializeTileSet(allTileSets[position])
        Toast.makeText(applicationContext, "List: ${parent?.getItemAtPosition(position)}" , Toast.LENGTH_SHORT).show()
    }

    /**
     * A native method that is implemented by the 'remotehotkey' native library,
     * which is packaged with this application.
     */
    external fun stringFromJNI(): String

    companion object {
        private val TAG = MainActivity::class.java.simpleName
        private const val MULTIPLE_PERMISSIONS_REQUEST = 139
        @RequiresApi(Build.VERSION_CODES.S)
        val PERMISSIONS_LIST = arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.SEND_SMS,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_ADVERTISE
        )

        // Used to load the 'remotehotkey' library on application startup.
        init {
            System.loadLibrary("remotehotkey")
        }
    }
}