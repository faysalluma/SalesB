package com.groupec.salesb.feature.home

import java.text.SimpleDateFormat
import java.util.Locale
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

        assertEquals(7, state?.daysRemaining)
        assertEquals(ExpirationBannerSeverity.WARNING, state?.severity)
    }

    @Test
    fun bannerIsRedThreeDaysBeforeExpiration() {
        val state = expirationBannerState("2026-07-15", format.parse("2026-07-12")!!)

        assertEquals(3, state?.daysRemaining)
        assertEquals(ExpirationBannerSeverity.URGENT, state?.severity)
    }

    @Test
    fun bannerRemainsRedAfterExpiration() {
        val state = expirationBannerState("2026-07-10", format.parse("2026-07-12")!!)

        assertEquals(-2, state?.daysRemaining)
        assertEquals(ExpirationBannerSeverity.URGENT, state?.severity)
    }
}
