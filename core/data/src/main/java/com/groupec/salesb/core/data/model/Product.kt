package com.groupec.salesb.core.data.model

import com.groupec.salesb.core.model.data.Product
import com.groupec.salesb.core.network.model.ProductItemResponse
import com.groupec.salesb.core.network.model.ProductResponse


fun ProductResponse.toProductList() : List<Product> = products.map { it.toProduct() }

fun ProductItemResponse.toProduct() = Product(
    id = id,
    datecreation = datecreation,
    reference = reference,
    libelle = libelle,
    description = description,
    image = image,
    prixht = prixht,
    prixttc = prixttc,
    qtestock = qtestock,
    stockmini = stockmini,
    categorieid = categorie?.id,
    categorielibelle = categorie?.libelle,
    rayonid = rayonid,
    fournisseurid = fournisseurid,
    tvaid = tvaid,
    datemodif = datemodif,
    userid = user?.id,
    username = user?.nomprenom
)

