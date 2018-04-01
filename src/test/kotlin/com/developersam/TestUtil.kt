@file:JvmName(name = "TestUtil")

package com.developersam

import java.util.Random

/**
 * [generateRandomCharacters] generates a random sequence of characters with
 * random length of at most 100.
 */
private fun generateRandomCharacters(): CharArray {
    val random = Random()
    val randomStringLength = random.nextInt(100)
    val randomChars = CharArray(randomStringLength)
    for (i in 0 until randomStringLength) {
        randomChars[i] = random.nextInt(1024).toChar()
    }
    return randomChars
}

/**
 * [generateRandomString] generate a random string with random length of at
 * most 100.
 */
internal fun generateRandomString(): String =
        String(chars = generateRandomCharacters())