package com.groupec.salesb.feature.home

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import kotlin.test.assertFailsWith

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {

/*// Act & Assert
        val thrownException = assertFailsWith<Exception> {
            repository.getDetailProduct(productId).first()
        }

        // Assert the exception message
        assertEquals("Failed to get product details", thrownException.message)*/
        assertEquals(4, 2 + 2)
    }
}