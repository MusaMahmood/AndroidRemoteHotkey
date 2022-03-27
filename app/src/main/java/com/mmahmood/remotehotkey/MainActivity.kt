package com.mmahmood.remotehotkey

import android.R
import android.bluetooth.*
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseData
import android.bluetooth.le.AdvertiseSettings
import android.bluetooth.le.BluetoothLeAdvertiser
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.ParcelUuid
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.mmahmood.remotehotkey.databinding.ActivityMainBinding
import java.util.*


class MainActivity : AppCompatActivity() {
    // TODO [] Make adjustable in settings
    private var mRows = 6
    private var mCols = 8

    private lateinit var binding: ActivityMainBinding
    // Bluetooth API
    private lateinit var bluetoothManager: BluetoothManager
    private var bluetoothGattServer: BluetoothGattServer? = null
    // Collection of notification subscribers:
    private val registeredDevices = mutableSetOf<BluetoothDevice>()
    private var mGattActive = false
    // If you want to limit number of registered devices:
//    private var registeredDeviceLimit = 1

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

        // Code adapted from - https://stackoverflow.com/questions/51430129/create-grid-n-%C3%97-n-in-android-constraintlayout-with-variable-number-of-n
        val layout = binding.conlayout

        val color1 = ContextCompat.getColor(this, R.color.holo_blue_bright)
        val color2 = ContextCompat.getColor(this, R.color.holo_blue_light)
        var textView: TextView
        var lp: ConstraintLayout.LayoutParams
        var id: Int
        val idArray = Array(mRows) { IntArray(mCols) }
        val cs = ConstraintSet()

        // TODO: Replace TextView with ImageViews or other custom view
            // Pull characteristics from settings/saved data
        // Add our views to the ConstraintLayout.
        for (iRow in 0 until mRows) {
            for (iCol in 0 until mCols) {
                textView = TextView(this)
                lp = ConstraintLayout.LayoutParams(
                    ConstraintSet.MATCH_CONSTRAINT,
                    ConstraintSet.MATCH_CONSTRAINT
                )
                id = View.generateViewId()
                idArray[iRow][iCol] = id
                textView.id = id
                textView.text = id.toString()
                textView.gravity = Gravity.CENTER
                textView.setBackgroundColor(if ((iRow + iCol) % 2 == 0) color1 else color2)
                layout.addView(textView, lp)
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
                findViewById<TextView>(idArray[iRow][iCol]).setOnClickListener {
                    sendCommand(idArray[iRow][iCol], iRow, iCol)
                }
            }
        }

        cs.applyTo(layout)

        // Initialize Bluetooth:
        bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager

        initGattServer()
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
        // TODO use lookup table to send appropriate command.
        // Data format = [id, command b0, command b1]
        sendBluetoothCommandUpdate(byteArrayOf(id.toByte(), 0x00, 0x01))
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

    override fun onResume() {
        super.onResume()
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

    /**
     * A native method that is implemented by the 'remotehotkey' native library,
     * which is packaged with this application.
     */
    external fun stringFromJNI(): String

    companion object {
        private val TAG = MainActivity::class.java.simpleName
        // Used to load the 'remotehotkey' library on application startup.
        init {
            System.loadLibrary("remotehotkey")
        }
    }
}