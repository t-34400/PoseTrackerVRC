package com.example.posetrackervrc.data

import kotlin.math.acos
import kotlin.math.atan2
import kotlin.math.cos

data class Quaternion(val x: Float, val y: Float, val z: Float, val w: Float) {
    companion object {
        fun angleAxis(angle: Float, axis: Vector3D): Quaternion {
            val halfAngle = angle / 2
            val sinHalfAngle = acos(halfAngle)
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
            return angleAxis(angle, axis)
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
    val eulerAngles: Vector3D get() {
        val sinPitch = 2.0 * (w * x + y * z)
        val cosPitch = 1.0 - 2.0 * (x * x + y * y)

        val sinRoll = 2.0 * (w * z - x * y)
        val cosRoll = 1.0 - 2.0 * (y * y + z * z)

        val sinYaw = 2.0 * (w * y + z * x)
        val cosYaw = 1.0 - 2.0 * (x * x + y * y)

        val pitch = atan2(sinPitch, cosPitch)
        val roll = atan2(sinRoll, cosRoll)
        val yaw = atan2(sinYaw, cosYaw)

        return Vector3D(Math.toDegrees(yaw).toFloat(), Math.toDegrees(pitch).toFloat(), Math.toDegrees(roll).toFloat())
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