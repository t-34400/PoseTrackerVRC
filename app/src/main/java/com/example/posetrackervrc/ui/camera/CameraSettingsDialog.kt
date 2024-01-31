package com.example.posetrackervrc.ui.camera

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.posetrackervrc.ui.components.SettingsDialog
import com.example.posetrackervrc.ui.udp.UDPSettingsComponent
import com.example.posetrackervrc.viewmodel.UDPViewModel

@Composable
fun CameraSettingsDialog(
    modifier: Modifier = Modifier,
    udpViewModel: UDPViewModel = viewModel(),
    poseViewModel: PoseViewModel = viewModel(),
    onDismissRequest: () -> Unit,
) {
    val remoteAddressInput = remember { mutableStateOf(udpViewModel.remoteAddress) }
    val remotePortInput = remember { mutableStateOf(udpViewModel.remotePort.toString()) }
    val shoulderWidthInput = remember { mutableStateOf(poseViewModel.shoulderWidth.value.toString()) }

    SettingsDialog(
        modifier = modifier,
        title = "Settings",
        onDismissRequest = {
            val port = remotePortInput.value.toIntOrNull() ?: udpViewModel.remotePort
            udpViewModel.updateRemoteClientInfo(
                address = remoteAddressInput.value,
                port = port
            )
            val shoulderWidth = shoulderWidthInput.value.toFloatOrNull() ?: poseViewModel.shoulderWidth.value
            poseViewModel.setShoulderWidth(shoulderWidth)
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
        Spacer(modifier = Modifier.height(20.dp))
        PoseSettingsComponent(
            shoulderWidth = shoulderWidthInput.value,
            onShoulderWidthChanged = { shoulderWidthString ->
                shoulderWidthInput.value = shoulderWidthString
            }
        )
    }
}

@Composable
fun PoseSettingsComponent(
    modifier: Modifier = Modifier,
    shoulderWidth: String,
    onShoulderWidthChanged: (String) -> Unit,
) {
    val focusManager = LocalFocusManager.current

    Column (
        modifier = modifier
    ) {
        Text(
            text = "Pose Estimation",
            style = MaterialTheme.typography.bodyLarge
        )
        OutlinedTextField(
            value = shoulderWidth,
            onValueChange = onShoulderWidthChanged,
            label = { Text("Shoulder Width") },
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Number
            ),
            keyboardActions = KeyboardActions(
                onNext = {
                    focusManager.moveFocus(FocusDirection.Next)
                }
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
        )
    }
}
