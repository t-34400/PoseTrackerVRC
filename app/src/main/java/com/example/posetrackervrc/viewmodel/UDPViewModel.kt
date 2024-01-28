package com.example.posetrackervrc.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class UDPViewModel : ViewModel() {
    var remoteAddress: String by mutableStateOf("127.0.0.1")
        private set

    var remotePort: Int by mutableIntStateOf(9000)
        private set

    fun updateRemoteClientInfo(address: String, port: Int) {
        viewModelScope.launch {
            remoteAddress = address
            remotePort = port
        }
    }
}