package com.example.posetrackervrc.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.posetrackervrc.VRChatOSCApplication
import com.example.posetrackervrc.data.repositories.UDPSettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

class UDPViewModel(
    private val udpSettingsRepository: UDPSettingsRepository
) : ViewModel() {
    companion object {
        val TAG = UDPViewModel::class.qualifiedName
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as VRChatOSCApplication)
                UDPViewModel(application.udpSettingsRepository)
            }
        }
    }

    val remoteAddress: StateFlow<String> =
        udpSettingsRepository.remoteAddress
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = "192.168.1.1"
            )
    val remotePort: StateFlow<Int> =
        udpSettingsRepository.remotePort
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = 9000
            )

    private val udpSocket: DatagramSocket = DatagramSocket()

    fun updateRemoteClientInfo(address: String, port: Int) {
        viewModelScope.launch {
            udpSettingsRepository.saveUDPSettings(address, port)
        }
    }

    fun sendBinaryData(data: ByteArray) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val inetAddress = InetAddress.getByName(remoteAddress.value)
                val packet = DatagramPacket(data, data.size, inetAddress, remotePort.value)
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