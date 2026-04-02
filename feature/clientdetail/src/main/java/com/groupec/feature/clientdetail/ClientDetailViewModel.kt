package com.groupec.feature.clientdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.groupec.salesb.core.FormUIState
import com.groupec.salesb.core.Result
import com.groupec.salesb.core.domain.client.SaveClientUseCase
import com.groupec.salesb.core.model.data.Client
import com.groupec.salesb.core.ui.ClientDataForm
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ClientDetailViewModel @Inject constructor(
    private val saveClientUseCase: SaveClientUseCase
) : ViewModel() {

    private val _addClientUiState = MutableStateFlow<FormUIState<*>>(FormUIState.Idle)
    val addClientUiState: StateFlow<FormUIState<*>> = _addClientUiState.asStateFlow()

    fun addClient(clientData: ClientDataForm) {
        _addClientUiState.value = FormUIState.Loading
        viewModelScope.launch {
            val clientModel = Client(
                id = clientData.id.takeIf { it.isNotEmpty() }?.toInt(),
                nomprenom = clientData.nomprenom,
                adresse = clientData.adresse.takeIf { it.isNotEmpty() },
                telephone = clientData.telephone.takeIf { it.isNotEmpty() }
            )

            when (val result = saveClientUseCase(clientModel)) {
                is Result.Success -> {
                    _addClientUiState.value = FormUIState.Success(Unit)
                }

                is Result.Error -> {
                    _addClientUiState.value =
                        FormUIState.Error(result.exception.message ?: "Error when adding client")
                }

                else -> {}
            }
        }
    }

    fun resetFlow() {
        _addClientUiState.value = FormUIState.Idle
    }
}
