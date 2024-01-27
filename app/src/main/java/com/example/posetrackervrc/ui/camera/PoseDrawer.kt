package com.example.posetrackervrc.ui.camera

import android.graphics.PointF
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseLandmark

private data class PoseConnection(
    val startIndex: Int,
    val endIndex: Int,
    val color: Color
)

private val poseConnections: List<PoseConnection> = listOf(
    PoseConnection(PoseLandmark.NOSE, PoseLandmark.LEFT_EYE_INNER, Color.Red),
    PoseConnection(PoseLandmark.LEFT_EYE_INNER, PoseLandmark.LEFT_EYE, Color.Red),
    PoseConnection(PoseLandmark.LEFT_EYE, PoseLandmark.LEFT_EYE_OUTER, Color.Red),
    PoseConnection(PoseLandmark.LEFT_EYE_OUTER, PoseLandmark.LEFT_EAR, Color.Red),

    PoseConnection(PoseLandmark.NOSE, PoseLandmark.RIGHT_EYE_INNER, Color.Blue),
    PoseConnection(PoseLandmark.RIGHT_EYE_INNER, PoseLandmark.RIGHT_EYE, Color.Blue),
    PoseConnection(PoseLandmark.RIGHT_EYE, PoseLandmark.RIGHT_EYE_OUTER, Color.Blue),
    PoseConnection(PoseLandmark.RIGHT_EYE_OUTER, PoseLandmark.RIGHT_EAR, Color.Blue),

    PoseConnection(PoseLandmark.LEFT_MOUTH, PoseLandmark.RIGHT_MOUTH, Color.White),
    PoseConnection(PoseLandmark.LEFT_SHOULDER, PoseLandmark.RIGHT_SHOULDER, Color.White),
    PoseConnection(PoseLandmark.LEFT_HIP, PoseLandmark.RIGHT_HIP, Color.White),

    PoseConnection(PoseLandmark.LEFT_SHOULDER, PoseLandmark.LEFT_ELBOW, Color.Red),
    PoseConnection(PoseLandmark.LEFT_ELBOW, PoseLandmark.LEFT_WRIST, Color.Red),
    PoseConnection(PoseLandmark.LEFT_WRIST, PoseLandmark.LEFT_THUMB, Color.Red),
    PoseConnection(PoseLandmark.LEFT_WRIST, PoseLandmark.LEFT_INDEX, Color.Red),
    PoseConnection(PoseLandmark.LEFT_WRIST, PoseLandmark.LEFT_PINKY, Color.Red),

    PoseConnection(PoseLandmark.RIGHT_SHOULDER, PoseLandmark.RIGHT_ELBOW, Color.Blue),
    PoseConnection(PoseLandmark.RIGHT_ELBOW, PoseLandmark.RIGHT_WRIST, Color.Blue),
    PoseConnection(PoseLandmark.RIGHT_WRIST, PoseLandmark.RIGHT_THUMB, Color.Blue),
    PoseConnection(PoseLandmark.RIGHT_WRIST, PoseLandmark.RIGHT_INDEX, Color.Blue),
    PoseConnection(PoseLandmark.RIGHT_WRIST, PoseLandmark.RIGHT_PINKY, Color.Blue),

    PoseConnection(PoseLandmark.LEFT_SHOULDER, PoseLandmark.LEFT_HIP, Color.Red),
    PoseConnection(PoseLandmark.LEFT_HIP, PoseLandmark.LEFT_KNEE, Color.Red),
    PoseConnection(PoseLandmark.LEFT_KNEE, PoseLandmark.LEFT_ANKLE, Color.Red),
    PoseConnection(PoseLandmark.LEFT_ANKLE, PoseLandmark.LEFT_HEEL, Color.Red),
    PoseConnection(PoseLandmark.LEFT_ANKLE, PoseLandmark.LEFT_FOOT_INDEX, Color.Red),

    PoseConnection(PoseLandmark.RIGHT_SHOULDER, PoseLandmark.RIGHT_HIP, Color.Blue),
    PoseConnection(PoseLandmark.RIGHT_HIP, PoseLandmark.RIGHT_KNEE, Color.Blue),
    PoseConnection(PoseLandmark.RIGHT_KNEE, PoseLandmark.RIGHT_ANKLE, Color.Blue),
    PoseConnection(PoseLandmark.RIGHT_ANKLE, PoseLandmark.RIGHT_HEEL, Color.Blue),
    PoseConnection(PoseLandmark.RIGHT_ANKLE, PoseLandmark.RIGHT_FOOT_INDEX, Color.Blue),
)

internal fun DrawScope.drawPoseConnections(
    offsets: Map<Int, Offset>,
    strokeWidth: Float,
) {
    poseConnections.forEach { poseConnection ->
        offsets[poseConnection.startIndex]?.let { startOffset ->
            offsets[poseConnection.endIndex]?.let { endOffset ->
                drawLine(
                    color = poseConnection.color,
                    start = startOffset,
                    end = endOffset,
                    strokeWidth = strokeWidth,
                    cap = StrokeCap.Round
                )
            }
        }
    }
}

internal fun DrawScope.drawPoseAnchors(
    offsets: Map<Int, Offset>,
    color: Color,
    radius: Float,
    strokeWidth: Float,
) {
    offsets.forEach { offset ->
        drawCircle(
            color = color,
            center = offset.value,
            radius = radius,
            style = Stroke(width = strokeWidth)
        )
    }
}

internal fun Pose.convertToCanvasOffset(
    canvasSize: androidx.compose.ui.geometry.Size,
    widthRatio: Float,
    heightRatio: Float
): Map<Int, Offset> {
    return this.allPoseLandmarks.associate { poseLandmark ->
        poseLandmark.landmarkType to
                poseLandmark.position.convertToCanvasOffset(canvasSize, widthRatio, heightRatio)
    }
}

private fun PointF.convertToCanvasOffset(
    canvasSize: androidx.compose.ui.geometry.Size,
    widthRatio: Float,
    heightRatio: Float
): Offset {
    return Offset(
        x = canvasSize.width - this.x * widthRatio,
        y = this.y * heightRatio
    )
}