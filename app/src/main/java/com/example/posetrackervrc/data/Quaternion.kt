package com.example.posetrackervrc.data

import android.util.Log
import kotlin.math.PI
import kotlin.math.acos
import kotlin.math.asin
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

data class Quaternion(val x: Float, val y: Float, val z: Float, val w: Float) {
    companion object {
        val identity: Quaternion = Quaternion(0f, 0f, 0f, 1f)
        fun angleAxis(angle: Float, axis: Vector3D): Quaternion {
            val halfAngle = angle / 2
            val sinHalfAngle = sin(halfAngle)
            return Quaternion(
                x = axis.x * sinHalfAngle,
                y = axis.y * sinHalfAngle,
                z = axis.z * sinHalfAngle,
                w = cos(halfAngle)
            )
        }

        fun rotationQuaternion(from: Vector3D, to: Vector3D): Quaternion {
            val dotProduct = from.dot(to)
            val lengthProduct = from.magnitude * to.magnitude

            val angle = acos(dotProduct / lengthProduct)
            val axis = Vector3D.getRotationAxis(from, to)
            return if (axis == Vector3D.zero) {
                identity
            } else {
                angleAxis(angle, axis)
            }
        }

        fun lookRotation(localYDir: Vector3D, localXDir: Vector3D): Quaternion {
            val yAlignmentRotation = rotationQuaternion(Vector3D.up, localYDir)
            val inverseRotatedX = (yAlignmentRotation.inverse * localXDir).run {
                this - this.dot(Vector3D.up) * Vector3D.up
            }
            val rotationAroundY = rotationQuaternion(Vector3D.right, inverseRotatedX)
            return yAlignmentRotation * rotationAroundY
        }
    }

    val inverse: Quaternion get () = Quaternion(-x, -y, -z, w)
    val normalized: Quaternion get () {
        val norm = sqrt(x * x + y * y + z * z + w * w)
        return Quaternion(x / norm, y / norm, z / norm, w / norm)
    }
    val eulerAngles: Vector3D get() {
        val q = normalized
        val sqw = q.w * q.w
        val sqx = q.x * q.x
        val sqy = q.y * q.y
        val sqz = q.z * q.z
        val unit = sqx + sqy + sqz + sqw
        val test = q.x * q.w - q.y * q.z

        if (test > 0.4995f * unit) {
            return Vector3D(
                y = 2f * atan2(q.y, q.x),
                x = (PI / 2f).toFloat(),
                z = 0f
            ).toDegrees()
        }
        if (test < -0.4995f * unit) {
            return Vector3D(
                y = -2f * atan2(q.y, q.x),
                x = (-PI / 2f).toFloat(),
                z = 0f
            ).toDegrees()
        }

        val rotatedQuaternion = Quaternion(q.w, q.z, q.x, q.y)
        return Vector3D(
            y = atan2(2f * rotatedQuaternion.x * rotatedQuaternion.w + 2f * rotatedQuaternion.y * rotatedQuaternion.z,
            1 - 2f * (rotatedQuaternion.z * rotatedQuaternion.z + rotatedQuaternion.w * rotatedQuaternion.w)),
            x = asin(2f * (rotatedQuaternion.x * rotatedQuaternion.z - rotatedQuaternion.w * rotatedQuaternion.y)),
            z = atan2(2f * rotatedQuaternion.x * rotatedQuaternion.y + 2f * rotatedQuaternion.z * rotatedQuaternion.w,
            1 - 2f * (rotatedQuaternion.y * rotatedQuaternion.y + rotatedQuaternion.z * rotatedQuaternion.z))
        ).toDegrees()
    }
    operator fun times(q: Quaternion): Quaternion {
        val x = w * q.x + x * q.w + y * q.z - z * q.y
        val y = w * q.y - x * q.z + y * q.w + z * q.x
        val z = w * q.z + x * q.y - y * q.x + z * q.w
        val w = w * q.w - x * q.x - y * q.y - z * q.z

        return Quaternion(x, y, z, w)        
    }

    operator fun times(vector: Vector3D): Vector3D {
        val tx = x * 2
        val ty = y * 2
        val tz = z * 2
        val txx = x * tx
        val tyy = y * ty
        val tzz = z * tz
        val txy = x * ty
        val txz = x * tz
        val tyz = y * tz
        val twx = w * tx
        val twy = w * ty
        val twz = w * tz

        val x = (1.0 - (tyy + tzz)) * vector.x + (txy - twz) * vector.y + (txz + twy) * vector.z
        val y = (txy + twz) * vector.x + (1.0 - (txx + tzz)) * vector.y + (tyz - twx) * vector.z
        val z = (txz - twy) * vector.x + (tyz + twx) * vector.y + (1.0 - (txx + tyy)) * vector.z

        return Vector3D(x.toFloat(), y.toFloat(), z.toFloat())
    }
}

operator fun Vector3D.times(q: Quaternion): Vector3D {
    val tx = q.x * 2
    val ty = q.y * 2
    val tz = q.z * 2
    val txx = q.x * tx
    val tyy = q.y * ty
    val tzz = q.z * tz
    val txy = q.x * ty
    val txz = q.x * tz
    val tyz = q.y * tz
    val twx = q.w * tx
    val twy = q.w * ty
    val twz = q.w * tz

    val x = (1.0 - (tyy + tzz)) * this.x + (txy - twz) * this.y + (txz + twy) * this.z
    val y = (txy + twz) * this.x + (1.0 - (txx + tzz)) * this.y + (tyz - twx) * this.z
    val z = (txz - twy) * this.x + (tyz + twx) * this.y + (1.0 - (txx + tyy)) * this.z

    return Vector3D(x.toFloat(), y.toFloat(), z.toFloat())
}

private fun Vector3D.toDegrees(): Vector3D {
    return Vector3D(
        x = (x / PI * 180).toFloat(),
        y = (y / PI * 180).toFloat(),
        z = (z / PI * 180).toFloat(),
    ).makePositive()
}

private fun Vector3D.makePositive(): Vector3D {
    val negativeFlip = Math.toDegrees(-0.0001)
    val positiveFlip = 360.0f + negativeFlip

    return Vector3D(
        x = when {
            x < negativeFlip -> x + 360.0f
            x > positiveFlip -> x - 360.0f
            else -> x
        },
        y = when {
            y < negativeFlip -> y + 360.0f
            y > positiveFlip -> y - 360.0f
            else -> y
        },
        z = when {
            z < negativeFlip -> z + 360.0f
            z > positiveFlip -> z - 360.0f
            else -> z
        }
    )
}
