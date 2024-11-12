package com.runner.helper

import android.bluetooth.BluetoothSocket
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.annotation.SuppressLint
import android.content.Context
import com.runner.helper.BluetoothUtil
import kotlin.Throws
import android.widget.Toast
import com.runner.R
import java.io.IOException
import java.io.OutputStream
import java.util.*

/**
 * Simple package for connecting a sunmi printer via Bluetooth
 */
object BluetoothUtil {
    private val PRINTER_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    private const val Innerprinter_Address = "00:11:22:33:44:55"
    var isBlueToothPrinter = false
    private var bluetoothSocket: BluetoothSocket? = null
    private val bTAdapter: BluetoothAdapter?
        private get() = BluetoothAdapter.getDefaultAdapter()

    private fun getDevice(bluetoothAdapter: BluetoothAdapter?): BluetoothDevice? {
        var innerprinter_device: BluetoothDevice? = null
        @SuppressLint("MissingPermission") val devices = bluetoothAdapter!!.bondedDevices
        for (device in devices) {
            if (device.address == Innerprinter_Address) {
                innerprinter_device = device
                break
            }
        }
        return innerprinter_device
    }

    @SuppressLint("MissingPermission")
    @Throws(IOException::class)
    private fun getSocket(device: BluetoothDevice): BluetoothSocket {
        val socket: BluetoothSocket
        socket = device.createRfcommSocketToServiceRecord(PRINTER_UUID)
        socket.connect()
        return socket
    }

    /**
     * connect bluetooth
     */
    fun connectBlueTooth(context: Context?): Boolean {
        if (bluetoothSocket == null) {
            if (bTAdapter == null) {
                Toast.makeText(context, R.string.toast_3, Toast.LENGTH_SHORT).show()
                return false
            }
            if (!bTAdapter!!.isEnabled) {
                Toast.makeText(context, R.string.toast_4, Toast.LENGTH_SHORT).show()
                return false
            }
            var device: BluetoothDevice
            if (getDevice(bTAdapter).also { device = it!! } == null) {
                Toast.makeText(context, R.string.toast_5, Toast.LENGTH_SHORT).show()
                return false
            }
            try {
                bluetoothSocket = getSocket(device)
            } catch (e: IOException) {
                Toast.makeText(context, R.string.toast_6, Toast.LENGTH_SHORT).show()
                return false
            }
        }
        return true
    }

    /**
     * disconnect bluethooth
     */
    fun disconnectBlueTooth(context: Context?) {
        if (bluetoothSocket != null) {
            try {
                val out = bluetoothSocket!!.outputStream
                out.close()
                bluetoothSocket!!.close()
                bluetoothSocket = null
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    /**
     * send esc cmd
     */
    fun sendData(bytes: ByteArray) {
        if (bluetoothSocket != null) {
            var out: OutputStream? = null
            try {
                out = bluetoothSocket!!.outputStream
                out.write(bytes, 0, bytes.size)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        } else {
            //TODO handle disconnect event
        }
    }
}