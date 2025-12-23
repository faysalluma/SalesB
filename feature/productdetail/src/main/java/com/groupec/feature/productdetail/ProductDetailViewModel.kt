package com.groupec.feature.productdetail

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.groupec.salesb.core.FormUIState
import com.groupec.salesb.core.Result
import com.groupec.salesb.core.UploadUtility
import com.groupec.salesb.core.asResult
import com.groupec.salesb.core.domain.category.GetCategoryUseCase
import com.groupec.salesb.core.domain.product.SaveProductUseCase
import com.groupec.salesb.core.domain.rayon.GetRayonUseCase
import com.groupec.salesb.core.domain.user.GetUserStoreUseCase
import com.groupec.salesb.core.model.data.Product
import com.groupec.salesb.core.model.data.UserStore
import com.groupec.salesb.core.ui.ProductDataForm
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val saveProductUseCase: SaveProductUseCase,
    private val getCategorieUsecase: GetCategoryUseCase,
    private val getRayonUseCase: GetRayonUseCase,
    private val getUserStoreUseCase: GetUserStoreUseCase
) : ViewModel() {

    private val _addProductUiState = MutableStateFlow<FormUIState<*>>(FormUIState.Idle)
    val addProductUiState : StateFlow<FormUIState<*>> = _addProductUiState.asStateFlow()

    private val _categoriesUiPairState = MutableStateFlow<List<Pair<String, String>>>(emptyList())
    val categoriesUiPairState = _categoriesUiPairState.asStateFlow()

    private val _rayonsUiPairState = MutableStateFlow<List<Pair<String, String>>>(emptyList())
    val rayonsUiPairState = _rayonsUiPairState.asStateFlow()

    private val _userStoreState = MutableStateFlow(UserStore())
    val userStoreState: StateFlow<UserStore> = _userStoreState.asStateFlow()

    init {
        viewModelScope.launch {
            _userStoreState.value = getUserStoreUseCase().first()
        }
    }
    fun getCategories() {
        viewModelScope.launch {
            getCategorieUsecase()
                .asResult()
                .collect { result ->
                    when (result) {
                        is Result.Success ->_categoriesUiPairState.value = result.data.map { Pair(it.id.toString(), it.libelle) }
                        else -> _categoriesUiPairState.value = emptyList()
                    }
                }
        }
    }

    fun getRayons() {
        viewModelScope.launch {
            getRayonUseCase("")
                .asResult()
                .collect { result ->
                    when (result) {
                        is Result.Success ->_rayonsUiPairState.value = result.data.map { Pair(it.id.toString(), it.libelle) }
                        else -> _rayonsUiPairState.value = emptyList()
                    }
                }
        }
    }

    fun addProduct(p: ProductDataForm, uri: Uri?) {
        _addProductUiState.value = FormUIState.Loading
        viewModelScope.launch {
            val productModel = Product(
                id = p.id.takeIf { it.isNotEmpty() }?.toInt(),
                reference = p.reference.takeIf { it.isNotEmpty() },
                libelle = p.libelle,
                description = p.description.takeIf { it.isNotEmpty() },
                prixttc = p.prixttc.toDouble(),
                qtestock = p.qtestock.takeIf { it.isNotEmpty() }?.toInt(),
                stockmini = p.stockmini.takeIf { it.isNotEmpty() }?.toInt(),
                categorieid = p.categorieid.takeIf { it.isNotEmpty() }?.toInt(),
                rayonid = p.rayonid.takeIf { it.isNotEmpty() }?.toInt(),
                fournisseurid = p.fournisseurid.takeIf { it.isNotEmpty() }?.toInt(),
                image = p.image.takeIf { it.isNotEmpty() }
            )

            when (val result = saveProductUseCase(productModel, uri)) {
                is Result.Success -> {
                    _addProductUiState.value = FormUIState.Success(Unit)
                }

                is Result.Error -> {
                    _addProductUiState.value = FormUIState.Error(result.exception.message ?: "Error when adding product")
                }
                else -> {}
            }
        }
    }

    fun resetFlow() {
        _addProductUiState.value = FormUIState.Idle
    }

    fun deleteImageFromCache(context: Context, filename: String) {
        UploadUtility.deleteImageFromCache(context, filename)
    }
}
