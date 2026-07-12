package com.groupec.salesb.feature.home

import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ExpirationBannerTest {
    private val format = SimpleDateFormat("yyyy-MM-dd", Locale.ROOT)

    @Test
    fun bannerIsHiddenWhenExpirationIsMoreThanOneWeekAway() {
        assertNull(expirationBannerState("2026-07-20", format.parse("2026-07-12")!!))
    }

    @Test
    fun bannerIsOrangeFromSevenToFourDaysBeforeExpiration() {
        val state = expirationBannerState("2026-07-19", format.parse("2026-07-12")!!)

        assertEquals(7L, state?.daysRemaining)
        assertEquals(ExpirationBannerSeverity.WARNING, state?.severity)
    }

    @Test
    fun bannerIsRedThreeDaysBeforeExpiration() {
        val state = expirationBannerState("2026-07-15", format.parse("2026-07-12")!!)

        assertEquals(3L, state?.daysRemaining)
        assertEquals(ExpirationBannerSeverity.URGENT, state?.severity)
    }

    @Test
    fun bannerRemainsRedAfterExpiration() {
        val state = expirationBannerState("2026-07-10", format.parse("2026-07-12")!!)

        assertEquals(-2L, state?.daysRemaining)
        assertEquals(ExpirationBannerSeverity.URGENT, state?.severity)
    }

    @Test
    fun bannerCountsCalendarDaysAcrossDaylightSavingTime() {
        val previousTimeZone = TimeZone.getDefault()
        try {
            TimeZone.setDefault(TimeZone.getTimeZone("America/New_York"))
            val localFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ROOT)
            val state = expirationBannerState("2026-03-09", localFormat.parse("2026-03-05")!!)

            assertEquals(4L, state?.daysRemaining)
            assertEquals(ExpirationBannerSeverity.WARNING, state?.severity)
        } finally {
            TimeZone.setDefault(previousTimeZone)
        }
    }

    @Test
    fun bannerAcceptsIsoTimestampWithNegativeOffset() {
        val state = expirationBannerState(
            "2026-07-19T00:00:00-04:00",
            format.parse("2026-07-12")!!,
        )

        assertEquals(7L, state?.daysRemaining)
        assertEquals(ExpirationBannerSeverity.WARNING, state?.severity)
    }
}
