package com.groupec.salesb.core.domain.categorie

import com.groupec.salesb.core.data.repository.CategorieRepository
import com.groupec.salesb.core.model.data.Categorie
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCategorieUsecase @Inject constructor(private val categorieRepository: CategorieRepository) {
    operator fun invoke() : Flow<List<Categorie>> = categorieRepository.getCategories()
}