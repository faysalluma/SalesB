package com.groupec.salesb.core

import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalCoroutinesApi::class)
class CommonExtensionsTest {

    private val initialLocale: Locale = Locale.getDefault()

    @AfterEach
    fun tearDown() {
        Locale.setDefault(initialLocale)
    }

    @Test
    fun `normalizeDecimalSeparator should keep only digits and one decimal separator`() {
        assertEquals("1234.56", "1 234,56 €".normalizeDecimalSeparator())
        assertEquals("12.34", "12.34.56".normalizeDecimalSeparator())
    }

    @Test
    fun `autoRound should keep two decimals for integers and trim other trailing zeros`() {
        assertEquals("10.00", 10.0.autoRound())
        assertEquals("10.5", 10.50.autoRound())
        assertEquals("10.57", 10.567.autoRound())
    }

    @Test
    fun `formatAmount should use locale and force french thousands separator to dot`() {
        Locale.setDefault(Locale.FRANCE)

        assertEquals("1.234,50", 1234.5.formatAmount(forceStyleFrenchUseDot = true))
    }

    @Test
    fun `convertDateFormat should use french locale`() {
        Locale.setDefault(Locale.FRANCE)

        assertEquals("2026-05-01", "01/05/2026".convertToServerDateFormat())
        assertEquals("01/05/2026", "2026-05-01".convertToViewDateFormat())
    }

    @Test
    fun `date helpers should format and compare days`() {
        val first = date(year = 2026, month = Calendar.MAY, day = 1, hour = 8)
        val sameDay = date(year = 2026, month = Calendar.MAY, day = 1, hour = 22)
        val otherDay = date(year = 2026, month = Calendar.MAY, day = 2, hour = 8)

        assertEquals("2026-05-01", first.toDateString("yyyy-MM-dd"))
        assertTrue(first.sameDay(sameDay))
        assertEquals(false, first.sameDay(otherDay))
    }

    @Test
    fun `asResult should emit loading then success values`() = runTest {
        val emissions = flow {
            emit(1)
            emit(2)
        }.asResult().toList()

        assertEquals(Result.Loading, emissions[0])
        assertEquals(Result.Success(1), emissions[1])
        assertEquals(Result.Success(2), emissions[2])
    }

    @Test
    fun `asResult should emit error when flow throws`() = runTest {
        val exception = IllegalStateException("boom")
        val emissions = flow<Int> {
            throw exception
        }.asResult().toList()

        assertEquals(Result.Loading, emissions[0])
        assertEquals(Result.Error(exception), emissions[1])
    }

    private fun date(year: Int, month: Int, day: Int, hour: Int): Date {
        return Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
            set(Calendar.DAY_OF_MONTH, day)
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time
    }
}
