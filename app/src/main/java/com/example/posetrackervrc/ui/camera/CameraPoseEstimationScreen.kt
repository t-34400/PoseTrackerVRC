package com.example.posetrackervrc.ui.camera

import android.util.Size
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.posetrackervrc.VRChatOSCApplication
import com.example.posetrackervrc.data.osc.PoseQueue
import com.example.posetrackervrc.data.osc.convertToOSCDatagrams
import com.example.posetrackervrc.data.osc.convertToRealCoordinates
import com.example.posetrackervrc.data.repositories.PoseEstimationSettingsRepository
import com.example.posetrackervrc.viewmodel.UDPViewModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseDetection
import com.google.mlkit.vision.pose.defaults.PoseDetectorOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.Executors

class PoseViewModel(
    private val poseEstimationSettingsRepository: PoseEstimationSettingsRepository
) : ViewModel() {
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as VRChatOSCApplication)
                PoseViewModel(application.poseEstimationSettingsRepository)
            }
        }
    }

    private val executor = Executors.newSingleThreadExecutor()
    private val options = PoseDetectorOptions.Builder()
        .setDetectorMode(PoseDetectorOptions.STREAM_MODE)
        .build()
    private val poseDetector = PoseDetection.getClient(options)

    private val _poseResult = mutableStateOf<Pose?>(null)
    private val _sourceImageSize = mutableStateOf(Size(1,1))
    val poseResult: State<Pose?> get() = _poseResult
    val sourceImageSize: State<Size> get() = _sourceImageSize
    val shoulderWidth: StateFlow<Float> =
        poseEstimationSettingsRepository.shoulderWidth
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = 45.0f
            )
    val zAdjustmentFactor: StateFlow<Float> =
        poseEstimationSettingsRepository.zAdjustmentFactor
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = 0.2f
            )

    fun setPoseEstimationSettings(shoulderWidth: Float, zAdjustmentFactor: Float) {
        if (shoulderWidth <= 0.0f) {
            return
        }
        viewModelScope.launch {
            poseEstimationSettingsRepository.savePoseEstimationSettings(shoulderWidth, zAdjustmentFactor)
        }
    }

    @androidx.annotation.OptIn(ExperimentalGetImage::class)
    fun startPoseDetection(cameraController: CameraController) {
        cameraController.setImageAnalysisAnalyzer(executor) { imageProxy ->
            imageProxy.image?.let { image ->
                val size = when (imageProxy.imageInfo.rotationDegrees) {
                    0, 180 -> Size(image.width, image.height)
                    else -> Size(image.height, image.width)
                }
                val inputImage = InputImage.fromMediaImage(
                    image,
                    imageProxy.imageInfo.rotationDegrees
                )
                poseDetector.process(inputImage)
                    .addOnCompleteListener {
                        imageProxy.close()
                    }
                    .addOnSuccessListener { result ->
                        viewModelScope.launch(Dispatchers.Main) {
                            _poseResult.value = result
                            _sourceImageSize.value = size
                        }
                    }
                    .addOnFailureListener { _ ->
                        viewModelScope.launch(Dispatchers.Main) {
                            _poseResult.value = null
                        }
                    }
            }
        }
    }
    override fun onCleared() {
        executor.shutdown()
        poseDetector.close()
        super.onCleared()
    }
}

@androidx.annotation.OptIn(ExperimentalGetImage::class)
@Composable
fun CameraPoseEstimationScreen(
    modifier: Modifier = Modifier,
    udpViewModel: UDPViewModel = viewModel(),
    poseViewModel: PoseViewModel = viewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current

    val shoulderWidth by poseViewModel.shoulderWidth.collectAsState()
    val zAdjustmentFactor by poseViewModel.zAdjustmentFactor.collectAsState()

    val pose by poseViewModel.poseResult
    val sourceImageSize by poseViewModel.sourceImageSize
    val poseQueue = remember {
        PoseQueue(
            size = 10,
            validCountThreshold = 8,
        )
    }

    val cameraController: CameraController = remember { LifecycleCameraController(context) }.apply {
        bindToLifecycle(lifecycleOwner)
        cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
    }
    val previewView: PreviewView = remember { PreviewView(context) }.apply {
        controller = cameraController
    }
    LaunchedEffect(true) {
        poseViewModel.startPoseDetection(cameraController)
    }
    LaunchedEffect(pose) {
        poseQueue.add(pose?.convertToRealCoordinates(shoulderWidth / 100, zAdjustmentFactor))
        withContext(Dispatchers.IO) {
            val oscDatagrams = convertToOSCDatagrams(poseQueue)
            oscDatagrams.forEach { datagram ->
                udpViewModel.sendBinaryData(datagram)
            }
        }
    }

    Box(
        modifier = modifier
    ) {
        AndroidView(
            factory = { previewView },
            modifier = Modifier.fillMaxSize()
        )
        pose?.let { pose ->
            PoseOverlay(
                modifier = Modifier.fillMaxSize(),
                pose = pose,
                sourceImageSize = sourceImageSize
            )
        }
    }
}

@Composable
fun PoseOverlay(
    modifier: Modifier = Modifier,
    pose: Pose,
    sourceImageSize: Size
) {
    val connectionStrokeWidth = with(LocalDensity.current) { 2.sp.toPx() }
    val anchorColor = Color.White
    val anchorStrokeWidth = with(LocalDensity.current) { 2.sp.toPx() }
    val anchorRadius = with(LocalDensity.current) { 5.sp.toPx() }

    Canvas(
        modifier = modifier
    ) {
        val widthRatio = size.width / sourceImageSize.width
        val heightRatio = size.height / sourceImageSize.height
        val offsets = pose.convertToCanvasOffset(size, widthRatio, heightRatio)

        drawPoseConnections(offsets, connectionStrokeWidth)
        drawPoseAnchors(offsets, anchorColor, anchorRadius, anchorStrokeWidth)
    }
}
