package com.groupec.salesb.core.data.model

import com.groupec.salesb.core.model.data.RecentActivityType
import com.groupec.salesb.core.network.model.RecentActivityItemResponse
import com.groupec.salesb.core.network.model.RecentActivityResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.Date

class RecentActivityMappingTest {
    @Test
    fun `response maps sale and output activities`() {
        val saleDate = Date(2_000L)
        val outputDate = Date(1_000L)
        val response = RecentActivityResponse(
            activities = listOf(
                RecentActivityItemResponse(
                    id = 2,
                    type = "sale",
                    date = saleDate,
                    amount = 120.0,
                ),
                RecentActivityItemResponse(
                    id = 1,
                    type = "output",
                    date = outputDate,
                    amount = 30.0,
                ),
            ),
        )

        val activities = response.toRecentActivityList()

        assertEquals(RecentActivityType.Sale, activities[0].type)
        assertEquals(saleDate, activities[0].occurredAt)
        assertEquals(RecentActivityType.Output, activities[1].type)
        assertEquals(null, activities[1].description)
    }
}
