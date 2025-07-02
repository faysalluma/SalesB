package com.groupec.feature.outputdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.groupec.salesb.core.FormUIState
import com.groupec.salesb.core.Result
import com.groupec.salesb.core.domain.category.SaveOutputUseCase
import com.groupec.salesb.core.model.data.Output
import com.groupec.salesb.core.ui.OutputDataForm
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OutPutDetailViewModel @Inject constructor(
    private val saveOutputUseCase: SaveOutputUseCase
) : ViewModel() {

    private val _addOutputUiState = MutableStateFlow<FormUIState<*>>(FormUIState.Idle)
    val addOutputUiState: StateFlow<FormUIState<*>> = _addOutputUiState.asStateFlow()

    fun addOutput(o: OutputDataForm) {
        _addOutputUiState.value = FormUIState.Loading
        viewModelScope.launch {
            val outputModel = Output(
                id = o.id.takeIf { it.isNotEmpty() }?.toInt(),
                description = o.description,
                prix = o.prix.toDouble()
            )

            when (val result = saveOutputUseCase(outputModel)) {
                is Result.Success -> {
                    _addOutputUiState.value = FormUIState.Success(Unit)
                }

                is Result.Error -> {
                    _addOutputUiState.value =
                        FormUIState.Error(result.exception.message ?: "Error when adding category")
                }

                else -> {}
            }
        }
    }

    fun resetFlow() {
        _addOutputUiState.value = FormUIState.Idle
    }
}
