package com.mmahmood.remotehotkey

import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothGattService
import java.util.*

object RemoteHotkeyService {

    // Service UUID:
    val REMOTE_HOTKEY_SERVICE: UUID = UUID.fromString("0000dd10-0000-1000-8000-00805f9b34fb")
    // Char UUID:
    val REMOTE_HOTKEY_CHAR: UUID = UUID.fromString("0000dd11-0000-1000-8000-00805f9b34fb")
    /* Mandatory Client Characteristic Config Descriptor */
    val CLIENT_CONFIG: UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")

    fun createHotkeyService(): BluetoothGattService {
        val service = BluetoothGattService(REMOTE_HOTKEY_SERVICE, BluetoothGattService.SERVICE_TYPE_PRIMARY)
        // Setup Char:
        val trainingChar = BluetoothGattCharacteristic(REMOTE_HOTKEY_CHAR, BluetoothGattCharacteristic.PROPERTY_READ or BluetoothGattCharacteristic.PROPERTY_NOTIFY, BluetoothGattCharacteristic.PERMISSION_READ)
        val configDescriptor = BluetoothGattDescriptor(CLIENT_CONFIG, BluetoothGattDescriptor.PERMISSION_READ or BluetoothGattDescriptor.PERMISSION_WRITE)
        trainingChar.addDescriptor(configDescriptor)
        service.addCharacteristic(trainingChar)

        return service
    }
}