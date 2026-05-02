package com.groupec.salesb.core.model

import com.groupec.salesb.core.model.data.UserStore
import com.groupec.salesb.core.model.data.others.PaymentType
import com.groupec.salesb.core.model.data.others.paymentTypeFromValue
import com.groupec.salesb.core.model.data.others.paymentTypeLibelleResFromValue
import com.groupec.salesb.core.model.data.others.paymentTypeValue
import com.groupec.salesb.core.model.data.toStringList
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class ModelHelpersTest {

    @Test
    fun `toStringList should split and trim values`() {
        assertEquals(listOf("admin", "sales", "stock"), " admin, sales ,stock ".toStringList())
    }

    @Test
    fun `getPrivileges should return empty list when privilege is blank`() {
        assertEquals(emptyList<String>(), UserStore(privilege = "").getPrivileges())
    }

    @Test
    fun `getPrivileges should split stored comma separated privileges`() {
        val userStore = UserStore(privilege = "admin, sales")

        assertEquals(listOf("admin", "sales"), userStore.getPrivileges())
    }

    @Test
    fun `payment type value should round trip ignoring case`() {
        assertEquals("cash", paymentTypeValue(PaymentType.Cash))
        assertEquals(PaymentType.Cash, paymentTypeFromValue("CASH"))
        assertEquals(PaymentType.Card.libelleRes, paymentTypeLibelleResFromValue("card"))
        assertNull(paymentTypeFromValue("mobile-money"))
    }
}
