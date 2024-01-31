package com.example.posetrackervrc.data

import kotlin.math.sqrt

data class Vector3D(val x: Float, val y: Float, val z: Float) {
    companion object {
        val zero = Vector3D(0.0f, 0.0f, 0.0f)
        val right = Vector3D(1.0f, 0.0f, 0.0f)
        val up = Vector3D(0.0f, 1.0f, 0.0f)
        val forward = Vector3D(0.0f, 0.0f, 1.0f)

        fun getRotationAxis(from: Vector3D, to: Vector3D): Vector3D {
            val crossProduct = from.cross(to)
            return crossProduct.normalized
        }
    }
    val sqrMagnitude: Float get() = x * x + y * y + z * z
    val magnitude: Float get() = sqrt(sqrMagnitude)
    val normalized: Vector3D
        get() {
            val magnitude = magnitude
            return if (magnitude != 0.0f) {
                Vector3D(x / magnitude, y / magnitude, z / magnitude)
            } else {
                zero
            }
        }

    operator fun plus(vector: Vector3D): Vector3D {
        return Vector3D(x + vector.x, y + vector.y, z + vector.z)
    }

    operator fun minus(vector: Vector3D): Vector3D {
        return Vector3D(x - vector.x, y - vector.y, z - vector.z)
    }

    operator fun times(multiplier: Float): Vector3D {
        return Vector3D(x * multiplier, y * multiplier, z * multiplier)
    }

    operator fun unaryMinus(): Vector3D {
        return Vector3D(-x, -y, -z)
    }

    fun dot(vector: Vector3D): Float {
        return x * vector.x + y * vector.y + z * vector.z
    }

    fun cross(vector: Vector3D): Vector3D {
        return Vector3D(
            x = y * vector.z - z * vector.y,
            y = z * vector.x - x * vector.z,
            z = x * vector.y - y * vector.x
        )
    }

    fun project(vector: Vector3D): Vector3D {
        val normalized = normalized
        return vector - normalized.dot(vector) * normalized
    }
}

operator fun Float.times(vector: Vector3D): Vector3D {
    return vector * this
}
