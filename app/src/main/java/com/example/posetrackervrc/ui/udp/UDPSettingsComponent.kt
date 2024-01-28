package com.example.posetrackervrc.ui.udp

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun UDPSettingsComponent(
    modifier: Modifier = Modifier,
    remoteAddress: String,
    onAddressChanged: (String) -> Unit,
    remotePort: String,
    onPortChanged: (String) -> Unit,
) {
    val focusManager = LocalFocusManager.current

    Column (
        modifier = modifier
    ) {
        Text(
            text = "UDP Remote Client",
            style = MaterialTheme.typography.bodyLarge
        )
        OutlinedTextField(
            value = remoteAddress,
            onValueChange = onAddressChanged,
            label = { Text("UDP Remote Address") },
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Next
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

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = remotePort,
            onValueChange = onPortChanged,
            label = { Text("UDP Remote Port") },
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done,
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
