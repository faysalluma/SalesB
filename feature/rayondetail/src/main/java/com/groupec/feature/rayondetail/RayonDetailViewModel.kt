package com.groupec.feature.rayondetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.groupec.salesb.core.FormUIState
import com.groupec.salesb.core.Result
import com.groupec.salesb.core.domain.rayon.SaveRayonUseCase
import com.groupec.salesb.core.model.data.Rayon
import com.groupec.salesb.core.ui.RayonDataForm
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RayonDetailViewModel @Inject constructor(
    private val saveRayonUseCase: SaveRayonUseCase
) : ViewModel() {

    private val _addRayonUiState = MutableStateFlow<FormUIState<*>>(FormUIState.Idle)
    val addRayonUiState: StateFlow<FormUIState<*>> = _addRayonUiState.asStateFlow()

    fun addRayon(r: RayonDataForm) {
        _addRayonUiState.value = FormUIState.Loading
        viewModelScope.launch {
            val rayonModel = Rayon(
                id = r.id.takeIf { it.isNotEmpty() }?.toInt(),
                libelle = r.libelle,
                description = r.description.takeIf { it.isNotEmpty() }
            )

            when (val result = saveRayonUseCase(rayonModel)) {
                is Result.Success -> {
                    _addRayonUiState.value = FormUIState.Success(Unit)
                }

                is Result.Error -> {
                    _addRayonUiState.value =
                        FormUIState.Error(result.exception.message ?: "Error when adding category")
                }

                else -> {}
            }
        }
    }

    fun resetFlow() {
        _addRayonUiState.value = FormUIState.Idle
    }
}
