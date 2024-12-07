package com.groupec.salesb.core.data.model

import com.groupec.salesb.core.model.data.Product
import com.groupec.salesb.core.network.model.ProductItemResponse
import com.groupec.salesb.core.network.model.ProductResponse


fun ProductResponse.toProductList() : List<Product> = products.map { it.toProduct() }

fun ProductItemResponse.toProduct() = Product(
    id, reference, libelle, description, image, prixht,
    prixttc, qtestock, stockmini, categorieid, rayonid, fournisseurid, tvaid, datemodif, userid
)

