package com.example.posetrackervrc

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.example.posetrackervrc.ui.main.MainSettingsDialog
import com.example.posetrackervrc.viewmodel.UDPViewModel

@Composable
fun MainTopBar (
    properties: DestinationProperties,
    onBackButtonClicked: (() -> Unit),
    udpViewModel: UDPViewModel,
) {

    when (properties) {
        MainDestinationProperties -> {
            val showSettingsDialog = remember { mutableStateOf(false) }
            MainTopBarBase(
                label = MainDestinationProperties.label,
                onBackButtonClicked = null,
                onSettingsButtonClicked = { showSettingsDialog.value = true }
            )
            if (showSettingsDialog.value) {
                MainSettingsDialog(
                    udpViewModel = udpViewModel,
                    onDismissRequest = { showSettingsDialog.value = false }
                )
            }
        }
        CameraPoseEstimationDestinationProperties -> {
            val showSettingsDialog = remember { mutableStateOf(false) }
            MainTopBarBase(
                label = CameraPoseEstimationDestinationProperties.label,
                onBackButtonClicked = onBackButtonClicked,
                onSettingsButtonClicked = { showSettingsDialog.value = true }
            )
            if (showSettingsDialog.value) {
                MainSettingsDialog(
                    udpViewModel = udpViewModel,
                    onDismissRequest = { showSettingsDialog.value = false }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainTopBarBase(
    label: String,
    onBackButtonClicked: (() -> Unit)? = null,
    onSettingsButtonClicked: (() -> Unit)? = null,
) {
    val navigationIcon: @Composable () -> Unit = {
        onBackButtonClicked?.let { onBackButtonClicked ->
            IconButton(onClick = onBackButtonClicked) {
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowLeft,
                    contentDescription = "Pop Back"
                )
            }
        }
    }

    val settingButtonAction: @Composable RowScope.() -> Unit = {
        onSettingsButtonClicked?.let { onSettingsButtonClicked ->
            IconButton(onClick = onSettingsButtonClicked) {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = "Settings"
                )
            }
        }
    }

    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),
        title = {
            Text(text = label)
        },
        navigationIcon = navigationIcon,
        actions = settingButtonAction
    )
}
