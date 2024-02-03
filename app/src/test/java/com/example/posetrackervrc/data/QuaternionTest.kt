package com.example.posetrackervrc.data

import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import kotlin.math.abs
import kotlin.math.asin

class QuaternionTest {

    @Test
    fun getInverse() {
        val q = Quaternion(1f, 2f, 3f, 4f)
        val expected = Quaternion(-1f, -2f, -3f, 4f)

        assertQuaternionEquals(expected, q.inverse, 0.02f)
    }

    @Test
    fun getEulerAngles() {
        val q = Quaternion(1f, 2f, 3f, 4f)
        println(q.eulerAngles)
        val expected = Vector3D(352.34f, 47.73f, 70.35f)

        assertVector3DEquals(expected, q.eulerAngles, 0.02f)
    }

    @Test
    fun times() {
    }

    @Test
    fun testTimes() {
    }

    private fun assertQuaternionEquals(expected: Quaternion, actual: Quaternion, precision: Float) {
        assertEquals(expected.x, actual.x, precision)
        assertEquals(expected.y, actual.y, precision)
        assertEquals(expected.z, actual.z, precision)
        assertEquals(expected.w, actual.w, precision)
    }

    private fun assertVector3DEquals(expected: Vector3D, actual: Vector3D, precision: Float) {
        assertEquals(expected.x, actual.x, precision)
        assertEquals(expected.y, actual.y, precision)
        assertEquals(expected.z, actual.z, precision)
    }
}