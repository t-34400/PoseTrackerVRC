package com.example.posetrackervrc.ui.camera

import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.LifecycleOwner

@androidx.annotation.OptIn(ExperimentalGetImage::class)
@Composable
fun CameraPoseEstimationScreen(
    modifier: Modifier = Modifier.fillMaxSize()
) {
    val context = LocalContext.current
    val lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current

    val cameraController: CameraController = remember { LifecycleCameraController(context) }.apply {
        bindToLifecycle(lifecycleOwner)
        cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
    }
    val previewView: PreviewView = remember { PreviewView(context) }.apply {
        controller = cameraController
    }
    Box(
        modifier = modifier
    ) {
        AndroidView(
            factory = { previewView },
            modifier = Modifier.fillMaxSize()
        )
    }
}