package com.example.posetrackervrc.ui.osc

import com.example.posetrackervrc.data.Vector3D
import java.nio.ByteBuffer
import java.nio.ByteOrder

fun Vector3D.convertToOSC(address: String): ByteArray {
    val addressBytes = padStringToMultipleOf4(address)
    val typeBytes = padStringToMultipleOf4(",fff")
    val valueBytes = ByteBuffer.allocate(4 * 3).run {
        this.order(ByteOrder.BIG_ENDIAN)
        this.putFloat(x).putFloat(y).putFloat(z)
        this.array()
    }

    return addressBytes + typeBytes + valueBytes
}

fun padStringToMultipleOf4(input: String): ByteArray {
    val stringBytes = input.toByteArray(Charsets.US_ASCII)
    val length = stringBytes.size

    val paddingSize = 4 - length % 4
    val paddedLength = length + paddingSize

    val paddedBytes = ByteArray(paddedLength)
    System.arraycopy(stringBytes, 0, paddedBytes, 0, length)

    repeat(paddingSize) {
        paddedBytes[length + it] = 0.toByte()
    }

    return paddedBytes
}