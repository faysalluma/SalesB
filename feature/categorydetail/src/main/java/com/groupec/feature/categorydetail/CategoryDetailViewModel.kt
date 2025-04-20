package com.groupec.feature.categorydetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.groupec.salesb.core.FormUIState
import com.groupec.salesb.core.Result
import com.groupec.salesb.core.domain.category.SaveCategoryUseCase
import com.groupec.salesb.core.model.data.Category
import com.groupec.salesb.core.ui.CategoryDataForm
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryDetailViewModel @Inject constructor(
    private val saveCategoryUseCase: SaveCategoryUseCase
) : ViewModel() {

    private val _addCategoryUiState = MutableStateFlow<FormUIState<*>>(FormUIState.Idle)
    val addCategoryUiState: StateFlow<FormUIState<*>> = _addCategoryUiState.asStateFlow()

    fun addCategory(c: CategoryDataForm) {
        _addCategoryUiState.value = FormUIState.Loading
        viewModelScope.launch {
            val categoryModel = Category(
                id = c.id.takeIf { it.isNotEmpty() }?.toInt(),
                libelle = c.libelle,
                description = c.description.takeIf { it.isNotEmpty() }
            )

            when (val result = saveCategoryUseCase(categoryModel)) {
                is Result.Success -> {
                    _addCategoryUiState.value = FormUIState.Success(Unit)
                }

                is Result.Error -> {
                    _addCategoryUiState.value =
                        FormUIState.Error(result.exception.message ?: "Error when adding category")
                }

                else -> {}
            }
        }
    }

    fun resetFlow() {
        _addCategoryUiState.value = FormUIState.Idle
    }
}
