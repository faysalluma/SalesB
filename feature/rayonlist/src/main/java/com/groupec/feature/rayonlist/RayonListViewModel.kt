package com.groupec.feature.rayonlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.groupec.salesb.core.FormUIState
import com.groupec.salesb.core.Result
import com.groupec.salesb.core.asResult
import com.groupec.salesb.core.domain.rayon.DeleteRayonUseCase
import com.groupec.salesb.core.domain.rayon.GetRayonUseCase
import com.groupec.salesb.core.model.data.Rayon
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class RayonListViewModel @Inject constructor(
    private val getRayonUseCase: GetRayonUseCase,
    private val deleteRayonUseCase: DeleteRayonUseCase,
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _rayonUiState = MutableStateFlow<RayonUiState>(RayonUiState.Loading)
    val rayonUiState: StateFlow<RayonUiState> = _rayonUiState.asStateFlow()

    private val _deleteRayonUiState = MutableStateFlow<FormUIState<*>>(FormUIState.Idle)
    val deleteRayonUiState: StateFlow<FormUIState<*>> = _deleteRayonUiState.asStateFlow()

    init {
        getRayons()
    }

    fun getRayons() {
        viewModelScope.launch {
            _searchQuery.flatMapLatest { query ->
                getRayonUseCase(query)
            }
                .asResult()
                .collect { result ->
                    _rayonUiState.value = when (result) {
                        is Result.Loading-> RayonUiState.Loading
                        is Result.Success -> {
                            if (result.data.isEmpty()){
                                RayonUiState.Empty
                            } else {
                                RayonUiState.Success(result.data)
                            }
                        }
                        is Result.Error -> RayonUiState.Error( result.exception.message ?: "Retrofit Unknown error")
                    }
                }
        }
    }

    fun updateSearchQuery(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun deleteRayon(id: Int) {
        _deleteRayonUiState.value = FormUIState.Loading
        viewModelScope.launch {
            when (val result = deleteRayonUseCase(id)) {
                is Result.Success -> {
                    _deleteRayonUiState.value = FormUIState.Success(Unit)
                }

                is Result.Error -> {
                    _deleteRayonUiState.value =
                        FormUIState.Error(result.exception.message ?: "Error when deleting category")
                }

                else -> {}
            }
        }
    }

    fun resetFlow() {
        _deleteRayonUiState.value = FormUIState.Idle
    }
}

sealed class RayonUiState {
    data object Loading : RayonUiState()
    data class Success(val rayons: List<Rayon>) : RayonUiState()
    data class Error(val message: String) : RayonUiState()
    data object Empty : RayonUiState()
}
