package com.groupec.salesb.core.model.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import java.util.Date

@Parcelize
@Serializable
data class Product(
    val id: Int ? = null,
    val datecreation: Date ? = null,
    val reference: String ? = null,
    val libelle: String,
    val description: String ? = null,
    val image: String ? = null,
    val prixht: Double ? = null,
    val prixttc: Double,
    val qtestock: Int ? = null,
    val stockmini: Int ? = null,
    val categorieid: Int ? = null,
    val categorielibelle: String ? = null,
    val rayonid: Int ? = null,
    val rayonlibelle: String ? = null,
    val fournisseurid: Int ? = null,
    val fournisseurlibelle: String ? = null,
    val tvaid: Int ? = null,
    val datemodif: Date ? = null,
    val userid: Int ? = null,
    val username: String ? = null
): Parcelable

// Note : Si je veux save dans la base
// Transformer le Product en sorte de ProductData (minify) pour envoyé à l'API retrofit lors du save
// Et ci garder le model complet comme recu (un peu ou non) du ResponseItem