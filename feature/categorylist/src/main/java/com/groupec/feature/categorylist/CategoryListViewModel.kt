package com.groupec.feature.categorylist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.groupec.salesb.core.Result
import com.groupec.salesb.core.UIState
import com.groupec.salesb.core.domain.category.DeleteCategoryUseCase
import com.groupec.salesb.core.domain.category.GetPagedCategoryUsecase
import com.groupec.salesb.core.model.data.Category
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class CategoryListViewModel @Inject constructor(
    private val getPagedCategoryUsecase: GetPagedCategoryUsecase,
    private val deleteCategoryUseCase: DeleteCategoryUseCase,
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isSearching = MutableStateFlow(false) // État de recherche
    val isSearching: StateFlow<Boolean> = _isSearching

    private val _deleteCategoryUiState = MutableStateFlow<UIState<*>>(UIState.Loading)
    val deleteCategoryUiState: StateFlow<UIState<*>> = _deleteCategoryUiState.asStateFlow()

    val pagedCategories: Flow<PagingData<Category>> = _searchQuery
        .flatMapLatest { query ->
            _isSearching.value = true // Indique qu'une recherche commence
            getPagedCategoryUsecase(query)
                .onCompletion {
                    _isSearching.value = false
                } // Recherche terminée
        }
        .cachedIn(viewModelScope) // Cache les données pour le cycle de vie de l'UI

    fun updateSearchQuery(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun deleteCategory(id: Int) {
        _deleteCategoryUiState.value = UIState.Loading
        viewModelScope.launch {
            when (val result = deleteCategoryUseCase(id)) {
                is Result.Success -> {
                    _deleteCategoryUiState.value = UIState.Success(Unit)
                }

                is Result.Error -> {
                    _deleteCategoryUiState.value =
                        UIState.Error(result.exception.message ?: "Error when deleting category")
                }

                else -> {}
            }
        }
    }
}
