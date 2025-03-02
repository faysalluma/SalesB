package com.groupec.salesb.core.data.model

import com.groupec.salesb.core.model.data.Sale
import com.groupec.salesb.core.model.data.SaleDetail
import com.groupec.salesb.core.network.model.SaleItemResponse
import com.groupec.salesb.core.network.model.SaleResponse
import com.groupec.salesb.core.toDate


fun SaleResponse.toSaleList(): List<Sale> = sales.map { it.toSale() }

fun SaleItemResponse.toSale() = Sale(
    id = id,
    datevente = datevente?.toDate(),
    totalprix = totalprix,
    datemodif = datemodif,
    userid = user?.id,
    username = user?.nomprenom,
    details = products.map { SaleDetail(it.id, it.libelle, it.qte, it.prix) }
)

