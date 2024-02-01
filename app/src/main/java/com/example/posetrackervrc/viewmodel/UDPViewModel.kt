package com.example.posetrackervrc.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

class UDPViewModel : ViewModel() {
    companion object {
        val TAG = UDPViewModel::class.qualifiedName
    }

    private val _remoteAddress = mutableStateOf("192.168.2.108")
    private val _remotePort = mutableIntStateOf(9000)
    var remoteAddress: String by _remoteAddress
    var remotePort: Int by _remotePort

    private val udpSocket: DatagramSocket = DatagramSocket()

    fun updateRemoteClientInfo(address: String, port: Int) {
        viewModelScope.launch {
            _remoteAddress.value = address
            _remotePort.intValue = port
        }
    }

    fun sendBinaryData(data: ByteArray) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val inetAddress = InetAddress.getByName(remoteAddress)
                val packet = DatagramPacket(data, data.size, inetAddress, remotePort)
                udpSocket.send(packet)
            } catch (e: Exception) {
                Log.e(TAG, e.toString())
            }
        }
    }

    override fun onCleared() {
        udpSocket.close()
        super.onCleared()
    }
}