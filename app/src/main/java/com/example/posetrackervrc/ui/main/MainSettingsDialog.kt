package com.example.posetrackervrc.ui.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.posetrackervrc.ui.components.SettingsDialog
import com.example.posetrackervrc.ui.udp.UDPSettingsComponent
import com.example.posetrackervrc.viewmodel.UDPViewModel

@Composable
fun MainSettingsDialog(
    modifier: Modifier = Modifier,
    udpViewModel: UDPViewModel,
    onDismissRequest: () -> Unit,
) {
    val remoteAddress by udpViewModel.remoteAddress.collectAsState()
    val remotePort by udpViewModel.remotePort.collectAsState()

    val remoteAddressInput = remember { mutableStateOf(remoteAddress) }
    val remotePortInput = remember { mutableStateOf(remotePort.toString()) }

    LaunchedEffect (remoteAddress) {
        remoteAddressInput.value = remoteAddress
    }
    LaunchedEffect (remotePort) {
        remotePortInput.value = remotePort.toString()
    }

    SettingsDialog(
        modifier = modifier,
        title = "Settings",
        onDismissRequest = {
            val port = remotePortInput.value.toIntOrNull() ?: udpViewModel.remotePort.value
            udpViewModel.updateRemoteClientInfo(
                address = remoteAddressInput.value,
                port = port
            )
            onDismissRequest()
        }
    ) {
        UDPSettingsComponent(
            remoteAddress = remoteAddressInput.value,
            onAddressChanged = { address ->
                remoteAddressInput.value = address
            },
            remotePort = remotePortInput.value,
            onPortChanged = { portString ->
                remotePortInput.value = portString
            }
        )
    }
}