package com.example.posetrackervrc

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.posetrackervrc.ui.camera.CameraPoseEstimationScreen
import com.example.posetrackervrc.ui.main.MainScreen
import com.example.posetrackervrc.ui.video.VideoPoseEstimationScreen
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MainNavHost(
    navController: NavHostController,
    modifier: Modifier,
    snackbarHostState: SnackbarHostState
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
    val cameraPermissionState = rememberPermissionState(
        permission = Manifest.permission.CAMERA,
        onPermissionResult = { result ->
            if (result) {
                navController.navigateSingleTopTo(CameraPoseEstimationDestinationProperties.route)
            } else {
                scope.launch {
                    val snackbarResult = snackbarHostState
                        .showSnackbar(
                            message = "Permission required: Camera",
                            actionLabel = "Settings",
                            duration = SnackbarDuration.Short
                        )
                    when (snackbarResult) {
                        SnackbarResult.ActionPerformed -> {
                            val uri = Uri.fromParts("package", context.packageName, null)
                            intent.data = uri
                            context.startActivity(intent)
                        }
                        SnackbarResult.Dismissed -> {}
                    }
                }
            }
        },
    )

    NavHost(
        navController = navController,
        startDestination = MainDestinationProperties.route,
        modifier = modifier
    ) {
        composable(route = MainDestinationProperties.route) {
            MainScreen(
                onCameraButtonClick = {
                    when {
                        cameraPermissionState.status.isGranted -> {
                            navController.navigateSingleTopTo(CameraPoseEstimationDestinationProperties.route)
                        }
                        else -> {
                            cameraPermissionState.launchPermissionRequest()
                        }
                    }
                },
                onVideoButtonClick = {
                    navController.navigateSingleTopTo(VideoPoseEstimationDestinationProperties.route)
                }
            )
        }
        composable(route = CameraPoseEstimationDestinationProperties.route) {
            CameraPoseEstimationScreen()
        }
        composable(route = VideoPoseEstimationDestinationProperties.route) {
            VideoPoseEstimationScreen()
        }
    }
}

private fun NavHostController.navigateSingleTopTo(route: String) =
    this.navigate(route) {
        popUpTo(
            this@navigateSingleTopTo.graph.findStartDestination().id
        ) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
