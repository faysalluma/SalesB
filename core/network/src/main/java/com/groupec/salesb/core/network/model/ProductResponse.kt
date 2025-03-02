package com.groupec.salesb.core.network.model

import com.google.gson.annotations.SerializedName
import java.util.Date

data class ProductResponse(
    @SerializedName("products")
    val products : ArrayList<ProductItemResponse>
)
data class ProductItemResponse(
    @SerializedName("id")
    val id: Int ? = null,

    @SerializedName("datecreation")
    val datecreation: Date ? = null,

    @SerializedName("reference")
    val reference: String ? = null,

    @SerializedName("libelle")
    val libelle: String,

    @SerializedName("description")
    val description: String ? = null,

    @SerializedName("image")
    val image: String ? = null,

    @SerializedName("prixht")
    val prixht: Double ? = null,

    @SerializedName("prixttc")
    val prixttc: Double,

    @SerializedName("qtestock")
    val qtestock: Int ? = 0,

    @SerializedName("stockmini")
    val stockmini: Int ? = null,

    @SerializedName("categorie")
    val categorie: CategorieItemResponse ? = null,

    @SerializedName("rayonid")
    val rayonid: Int ? = null,

    @SerializedName("fournisseurid")
    val fournisseurid: Int ? = null,

    @SerializedName("tvaid")
    val tvaid: Int ? = null,

    @SerializedName("datemodif")
    val datemodif: Date ? = null,

    @SerializedName("user")
    val user: UserReducedResponse ? = null
)

data class ProductReducedResponse(
    @SerializedName("id")
    val id: Int,

    @SerializedName("libelle")
    val libelle: String,

    @SerializedName("qte")
    val qte: Double,

    @SerializedName("prix")
    val prix: Double
)