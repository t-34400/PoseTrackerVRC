package com.example.posetrackervrc.data.osc

import com.example.posetrackervrc.data.Quaternion
import com.example.posetrackervrc.data.Vector3D
import com.example.posetrackervrc.data.times
import com.google.mlkit.vision.common.PointF3D
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseLandmark
import kotlin.math.sqrt

data class PoseQueue(private val size: Int, private val validCountThreshold: Int, private val queue: ArrayDeque<Map<Int, Vector3D>?> = ArrayDeque()) {
    fun add(pose: Map<Int, Vector3D>?) {
        queue.add(pose)
        while (queue.size > size) {
            queue.removeFirstOrNull()
        }
    }

    fun hasEnoughPose(): Boolean {
        return queue.size >= validCountThreshold
    }

    operator fun get(landmarkIndex: Int): Vector3D? {
        val validPoses = queue.filterNotNull().mapNotNull { landmarks -> landmarks[landmarkIndex] }
        if (validPoses.count() < validCountThreshold) {
            return null
        }

        return validPoses.average()
    }
}

fun convertToOSCDatagrams(poseQueue: PoseQueue): List<ByteArray> {
    if (!poseQueue.hasEnoughPose()) {
        return listOf()
    }

    val datagrams = mutableListOf<ByteArray>()

    poseQueue[PoseLandmark.LEFT_EYE]?.let { leftEye ->
        poseQueue[PoseLandmark.RIGHT_EYE]?.let { rightEye ->
            val eyeMidpoint = (0.5f * (leftEye + rightEye)).also {
                datagrams.add(it.convertToOSC("/tracking/trackers/head/position"))
            }

            poseQueue[PoseLandmark.LEFT_MOUTH]?.let { leftMouth ->
                poseQueue[PoseLandmark.RIGHT_MOUTH]?.let { rightMouth ->
                    val mouthMidpoint = 0.5f * (leftMouth + rightMouth)

                    val headDir = eyeMidpoint - mouthMidpoint
                    val eyeDir = rightEye - leftEye
                    val headRotation = Quaternion.lookRotation(headDir, eyeDir)
                    datagrams.add(headRotation.eulerAngles.convertToOSC("/tracking/trackers/head/rotation"))
                }
            }
        }
    }

    val leftAnkle = poseQueue[PoseLandmark.LEFT_ANKLE]?.also {
        datagrams.add(it.convertToOSC("/tracking/trackers/3/position"))
    }
    val rightAnkle = poseQueue[PoseLandmark.RIGHT_ANKLE]?.also {
        datagrams.add(it.convertToOSC("/tracking/trackers/4/position"))
    }

    poseQueue[PoseLandmark.LEFT_KNEE]?.let { leftKnee ->
        datagrams.add(leftKnee.convertToOSC("/tracking/trackers/5/position"))

        leftAnkle?.let { leftAnkle ->
            poseQueue[PoseLandmark.LEFT_FOOT_INDEX]?.let { leftFootIndex ->
                val leftLegDir = leftAnkle - leftKnee
                val leftFootDir = leftFootIndex - leftAnkle
                val leftAnkleAxis = - leftLegDir.cross(leftFootDir)

                val footRotation = Quaternion.lookRotation(leftFootDir, leftAnkleAxis)
                datagrams.add(footRotation.eulerAngles.convertToOSC("/tracking/trackers/3/rotation"))

                val legRotation = Quaternion.lookRotation(leftLegDir, leftAnkle)
                datagrams.add(legRotation.eulerAngles.convertToOSC("/tracking/trackers/5/rotation"))
            }
        }
    }

    poseQueue[PoseLandmark.RIGHT_KNEE]?.let { rightKnee ->
        datagrams.add(rightKnee.convertToOSC("/tracking/trackers/6/position"))

        rightAnkle?.let { rightAnkle ->
            poseQueue[PoseLandmark.RIGHT_FOOT_INDEX]?.let { rightFootIndex ->
                val rightLegDir = rightAnkle - rightKnee
                val rightFootDir = rightFootIndex - rightAnkle
                val rightAnkleAxis = - rightLegDir.cross(rightFootDir)

                val footRotation = Quaternion.lookRotation(rightFootDir, rightAnkleAxis)
                datagrams.add(footRotation.eulerAngles.convertToOSC("/tracking/trackers/4/rotation"))

                val legRotation = Quaternion.lookRotation(rightLegDir, rightAnkle)
                datagrams.add(legRotation.eulerAngles.convertToOSC("/tracking/trackers/6/rotation"))
            }
        }
    }

    poseQueue[PoseLandmark.LEFT_ELBOW]?.let { leftElbow ->
        datagrams.add(leftElbow.convertToOSC("/tracking/trackers/7/position"))

        poseQueue[PoseLandmark.LEFT_WRIST]?.let { leftWrist ->
            poseQueue[PoseLandmark.LEFT_THUMB]?.let { leftThumb ->
                val leftArmDir = leftWrist - leftElbow
                val leftThumbDir = leftThumb - leftWrist
                val rotation = Quaternion.lookRotation(leftArmDir, -leftThumbDir)
                datagrams.add(rotation.eulerAngles.convertToOSC("/tracking/trackers/7/rotation"))
            }
        }
    }

    poseQueue[PoseLandmark.RIGHT_ELBOW]?.let { rightElbow ->
        datagrams.add(rightElbow.convertToOSC("/tracking/trackers/8/position"))

        poseQueue[PoseLandmark.RIGHT_WRIST]?.let { rightWrist ->
            poseQueue[PoseLandmark.RIGHT_THUMB]?.let { rightThumb ->
                val rightArmDir = rightWrist - rightElbow
                val rightThumbDir = rightThumb - rightWrist
                val rotation = Quaternion.lookRotation(rightArmDir, rightThumbDir)
                datagrams.add(rotation.eulerAngles.convertToOSC("/tracking/trackers/8/rotation"))
            }
        }
    }

    return datagrams
}

fun Pose.convertToRealCoordinates(
    shoulderWidth: Float,
    zAdjustmentFactor: Float
): Map<Int, Vector3D>? {
    val _shoulderPixelWidth = this.getPoseLandmark(PoseLandmark.LEFT_SHOULDER)?.let { leftShoulder ->
        this.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER)?.let { rightShoulder ->
            leftShoulder.position3D.distance(rightShoulder.position3D)
        }
    }
    return _shoulderPixelWidth?.let { shoulderPixelWidth ->
        if (shoulderPixelWidth == 0.0f) {
            return null
        }
        val pixelToRealRatio = shoulderWidth / shoulderPixelWidth
        this.allPoseLandmarks.associate { poseLandmark ->
            poseLandmark.landmarkType to
                    poseLandmark.position3D.convertToRealCoordinates(pixelToRealRatio, zAdjustmentFactor)
        }
    }
}

private fun PointF3D.convertToRealCoordinates(
    pixelToRealRatio: Float,
    zAdjustmentFactor: Float
): Vector3D {
    return Vector3D(
        x = - x * pixelToRealRatio,
        y = y * pixelToRealRatio,
        z = z * pixelToRealRatio * zAdjustmentFactor
    )
}

fun PointF3D.sqrDistance(pointF3D: PointF3D): Float {
    val xDelta = x - pointF3D.x
    val yDelta = y - pointF3D.y
    val zDelta = z - pointF3D.z
    return xDelta * xDelta + yDelta * yDelta + zDelta * zDelta
}

fun PointF3D.distance(pointF3D: PointF3D): Float {
    return sqrt(sqrDistance(pointF3D))
}

private fun Collection<Vector3D>.average(): Vector3D {
    val size = this.size
    var total = Vector3D(0f, 0f, 0f)
    this.forEach {  vector ->
        total += vector
    }
    return total / size
}