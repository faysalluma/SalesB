package com.groupec.salesb.core.data.repository


import com.groupec.salesb.core.model.data.Categorie
import kotlinx.coroutines.flow.Flow

interface CategorieRepository {
    fun getCategories(): Flow<List<Categorie>>
}