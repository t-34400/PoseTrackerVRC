package com.example.posetrackervrc.ui.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
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
    udpViewModel: UDPViewModel = viewModel(),
    onDismissRequest: () -> Unit,
) {
    val remoteAddress = remember { mutableStateOf(udpViewModel.remoteAddress) }
    val remotePort = remember { mutableIntStateOf(udpViewModel.remotePort) }

    SettingsDialog(
        modifier = modifier,
        title = "Settings",
        onDismissRequest = {
            udpViewModel.updateRemoteClientInfo(
                address = remoteAddress.value,
                port = remotePort.intValue
            )
            onDismissRequest()
        }
    ) {
        UDPSettingsComponent(
            remoteAddress = remoteAddress.value,
            onAddressChanged = { address ->
                remoteAddress.value = address
            },
            remotePort = remotePort.intValue.toString(),
            onPortChanged = { portString ->
                portString.toIntOrNull()?.let { port ->
                    remotePort.intValue = port
                }
            }
        )
    }
}