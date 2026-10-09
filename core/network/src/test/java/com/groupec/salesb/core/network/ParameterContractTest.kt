package com.groupec.salesb.core.network

import com.google.gson.Gson
import com.groupec.salesb.core.network.model.ParameterResponse
import com.groupec.salesb.core.network.retrofit.ApiService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

class ParameterContractTest {
    @Test
    fun `parameter update uses company route and business type multipart field`() {
        val method = ApiService::class.java.declaredMethods.single { it.name == "updateParameter" }
        assertEquals("parameter/{userid}/{entrepriseid}", method.getAnnotation(POST::class.java).value)
        val paths = method.parameterAnnotations.flatten().filterIsInstance<Path>().map { it.value }
        val parts = method.parameterAnnotations.flatten().filterIsInstance<Part>().map { it.value }
        assertEquals(listOf("userid", "entrepriseid"), paths)
        assertEquals(listOf("raisonsociale", "businessType", "ifu", "adresse", "telephone", "email",
            "website", "devise", "tva", ""), parts)
    }

    @Test
    fun `parameter response reads cash configuration and distinct company identifier`() {
        val result = Gson().fromJson("""{"parameter":{
            "id":42,"userid":7,"entrepriseid":8,"cash_enabled":1,
            "devise":"EUR","serviceview":1,"tva":null,"email":null
        }}""", ParameterResponse::class.java).parameter
        assertEquals(42, result.id)
        assertEquals(7, result.userid)
        assertEquals(8, result.entrepriseid)
        assertEquals(1, result.cashEnabled)
        assertNull(result.tva)
        assertNull(result.email)
    }
}
