package com.groupec.salesb.core.data.model

import com.groupec.salesb.core.network.model.CategoryItemResponse
import com.groupec.salesb.core.network.model.CategoryResponse
import com.groupec.salesb.core.network.model.ClientReducedResponse
import com.groupec.salesb.core.network.model.ParamItemResponse
import com.groupec.salesb.core.network.model.ParameterResponse
import com.groupec.salesb.core.network.model.ProductItemResponse
import com.groupec.salesb.core.network.model.ProductReducedResponse
import com.groupec.salesb.core.network.model.ProductResponse
import com.groupec.salesb.core.network.model.RayonItemResponse
import com.groupec.salesb.core.network.model.SaleItemResponse
import com.groupec.salesb.core.network.model.SaleResponse
import com.groupec.salesb.core.network.model.UserItemResponse
import com.groupec.salesb.core.network.model.UserReducedResponse
import com.groupec.salesb.core.network.model.UserResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.util.Date

class ResponseMappingTest {

    @Test
    fun `category response should map user metadata`() {
        val createdAt = Date(1_000)
        val response = CategoryResponse(
            arrayListOf(
                CategoryItemResponse(
                    id = 5,
                    datecreation = createdAt,
                    libelle = "Boissons",
                    description = "Rayon frais",
                    user = UserReducedResponse(id = 9, nomprenom = "Awa")
                )
            )
        )

        val category = response.toCategorieList().single()

        assertEquals(5, category.id)
        assertEquals(createdAt, category.datecreation)
        assertEquals("Boissons", category.libelle)
        assertEquals("Rayon frais", category.description)
        assertEquals(9, category.userid)
        assertEquals("Awa", category.username)
    }

    @Test
    fun `product response should map nested category rayon and user`() {
        val response = ProductResponse(
            arrayListOf(
                ProductItemResponse(
                    id = 11,
                    reference = "REF-1",
                    libelle = "Café",
                    description = "Arabica",
                    image = "coffee.png",
                    prixht = 900.0,
                    prixttc = 1000.0,
                    qtestock = 7,
                    stockmini = 2,
                    categorie = CategoryItemResponse(id = 3, libelle = "Epicerie"),
                    rayon = RayonItemResponse(id = 4, libelle = "A1"),
                    fournisseurid = 8,
                    tvaid = 2,
                    user = UserReducedResponse(id = 6, nomprenom = "Moussa")
                )
            )
        )

        val product = response.toProductList().single()

        assertEquals(11, product.id)
        assertEquals("REF-1", product.reference)
        assertEquals("Café", product.libelle)
        assertEquals(1000.0, product.prixttc, 0.0)
        assertEquals(3, product.categorieid)
        assertEquals("Epicerie", product.categorielibelle)
        assertEquals(4, product.rayonid)
        assertEquals("A1", product.rayonlibelle)
        assertEquals(6, product.userid)
        assertEquals("Moussa", product.username)
    }

    @Test
    fun `parameter response should map integer flags to booleans and defaults`() {
        val parameter = ParameterResponse(
            ParamItemResponse(
                id = 1,
                logo = null,
                raisonsociale = "SalesB",
                entreprisetype = 2,
                ifu = "IFU",
                adresse = "Cotonou",
                telephone = "123",
                email = "contact@salesb.test",
                website = null,
                devise = "XOF",
                tva = 18.0,
                expirationdate = "2026-12-31",
                offline = 1,
                showimageonproduct = 0,
                useintforpriceandamout = 1,
                activepaymentmode = 1,
                activeclient = 0,
                defaultpayment = null,
                activeprinter = 1,
                userid = 42
            )
        ).toParameter()

        assertTrue(parameter.offline)
        assertFalse(parameter.showimageonproduct)
        assertTrue(parameter.useintforpriceandamout)
        assertTrue(parameter.activepaymentmode)
        assertFalse(parameter.activeClient)
        assertTrue(parameter.activeprinter)
        assertEquals("", parameter.defaultpaymenttype)
        assertEquals(42, parameter.userid)
    }

    @Test
    fun `sale response should map date client user and details`() {
        val response = SaleResponse(
            arrayListOf(
                SaleItemResponse(
                    id = 12,
                    datevente = "2026-05-01 10:15:30",
                    totalprix = 2500.0,
                    client = ClientReducedResponse(
                        id = 2,
                        nomprenom = "Client Test",
                        adresse = "Adresse",
                        telephone = "999"
                    ),
                    paymenttype = "cash",
                    user = UserReducedResponse(id = 7, nomprenom = "Caissier"),
                    products = arrayListOf(
                        ProductReducedResponse(id = 4, libelle = "Produit", qte = 2.0, prix = 1250.0)
                    )
                )
            )
        )

        val sale = response.toSaleList().single()

        assertEquals(12, sale.id)
        assertEquals("2026-05-01", sale.datevente?.let { java.text.SimpleDateFormat("yyyy-MM-dd").format(it) })
        assertEquals(2500.0, sale.totalprix, 0.0)
        assertEquals(2, sale.clientid)
        assertEquals("Client Test", sale.clientName)
        assertEquals("cash", sale.paymenttype)
        assertEquals(7, sale.userid)
        assertEquals("Caissier", sale.username)
        assertEquals(4, sale.details.single().id)
        assertEquals(2.0, sale.details.single().qte, 0.0)
    }

    @Test
    fun `user response should map boolean flags and entity store variants`() {
        val response = UserItemResponse(
            id = 31,
            nomprenom = "Admin",
            email = "admin@salesb.test",
            password = "secret",
            reset_password = "code",
            reset_expires = "2026-05-02",
            adresse = "Cotonou",
            tel = "123",
            privilege = "admin,sales",
            actif = 1,
            firstlogin = 0,
            datecreation = Date(0)
        )

        val user = UserResponse(arrayListOf(response)).toUserList().single()
        val entity = response.toUserEntity()
        val store = response.toUserStore()

        assertEquals(31, user.id)
        assertTrue(user.actif)
        assertFalse(user.firstlogin)
        assertEquals(31, entity.id)
        assertFalse(entity.firstlogin)
        assertEquals("31", store.id)
        assertEquals("Admin", store.nomprenom)
        assertEquals("admin,sales", store.privilege)
        assertEquals("code", store.reset_password)
    }

    @Test
    fun `nullable nested objects should map to null domain metadata`() {
        val product = ProductItemResponse(
            id = 1,
            libelle = "Sans relation",
            prixttc = 100.0,
            categorie = null,
            rayon = null,
            user = null
        ).toProduct()

        assertNull(product.categorieid)
        assertNull(product.categorielibelle)
        assertNull(product.rayonid)
        assertNull(product.rayonlibelle)
        assertNull(product.userid)
        assertNull(product.username)
    }
}
