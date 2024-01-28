package com.example.posetrackervrc.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.posetrackervrc.ui.udp.UDPSettingsComponent
import com.example.posetrackervrc.viewmodel.UDPViewModel

@Composable
fun SettingsDialog(
    modifier: Modifier = Modifier,
    title: String,
    onDismissRequest: () -> Unit,
    titleGap: Float = 12f,
    components: @Composable () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismissRequest
    ) {
        Column(
            modifier = modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
            )
            Spacer(modifier = Modifier.height(titleGap.dp))

            Column (
                modifier = Modifier.padding(8.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                components()
            }

            Spacer(modifier = Modifier.height(titleGap.dp))
            Button(
                onClick = {
                    onDismissRequest()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(36.dp)
            ) {
                Text("OK")
            }
        }
    }
}