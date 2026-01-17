package com.groupec.feature.outputlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.groupec.salesb.core.FormUIState
import com.groupec.salesb.core.Result
import com.groupec.salesb.core.domain.category.DeleteOutputUseCase
import com.groupec.salesb.core.domain.output.GetOutputUseCase
import com.groupec.salesb.core.model.data.Output
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
class OutputListViewModel @Inject constructor(
    private val getOutputUseCase: GetOutputUseCase,
    private val deleteOutputUseCase: DeleteOutputUseCase,
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isSearching = MutableStateFlow(false) // État de recherche
    val isSearching: StateFlow<Boolean> = _isSearching

    private val _deleteOutputUiState = MutableStateFlow<FormUIState<*>>(FormUIState.Idle)
    val deleteOutputUiState: StateFlow<FormUIState<*>> = _deleteOutputUiState.asStateFlow()

    val pagedOutputs: Flow<PagingData<Output>> = _searchQuery
        .flatMapLatest { query ->
            _isSearching.value = true // Indique qu'une recherche commence
            getOutputUseCase(query)
                .onCompletion {
                    _isSearching.value = false
                } // Recherche terminée
        }
        .cachedIn(viewModelScope) // Cache les données pour le cycle de vie de l'UI

    fun updateSearchQuery(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun deleteOutput(id: Int) {
        _deleteOutputUiState.value = FormUIState.Loading
        viewModelScope.launch {
            when (val result = deleteOutputUseCase(id)) {
                is Result.Success -> {
                    _deleteOutputUiState.value = FormUIState.Success(Unit)
                }

                is Result.Error -> {
                    _deleteOutputUiState.value =
                        FormUIState.Error(result.exception.message ?: "Error when deleting category")
                }

                else -> {}
            }
        }
    }

    fun resetFlow() {
        _deleteOutputUiState.value = FormUIState.Idle
    }
}
