package com.groupec.feature.updatebusinessinfo

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.groupec.salesb.core.FormUIState
import com.groupec.salesb.core.Result
import com.groupec.salesb.core.UploadUtility
import com.groupec.salesb.core.domain.parameter.GetParameterUseCase
import com.groupec.salesb.core.domain.parameter.UpdateBusinessInfoUseCase
import com.groupec.salesb.core.model.data.Parameter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UpdateBusinessInfoViewModel @Inject constructor(
    private val getParameterUseCase: GetParameterUseCase,
    private val updateBusinessInfoUseCase: UpdateBusinessInfoUseCase
) : ViewModel() {

    private val _updateBusinessUiState = MutableStateFlow<FormUIState<*>>(FormUIState.Idle)
    val updateBusinessUiState: StateFlow<FormUIState<*>> = _updateBusinessUiState.asStateFlow()

    private val _parameterState = MutableStateFlow(Parameter())
    val parameterState: StateFlow<Parameter> = _parameterState.asStateFlow()

    init {
        getParameter()
    }

    fun getParameter() {
        viewModelScope.launch {
            try {
                val parameter = getParameterUseCase().first()
                _parameterState.value = parameter
            } catch (e: Exception) {
                Log.e("UpdateBusinessVM", "Error getting parameter", e)
            }
        }
    }

    fun updateBusinessInfo(form: UpdateBusinessFormData, uri: Uri?) {
        _updateBusinessUiState.value = FormUIState.Loading
        viewModelScope.launch {
            try {
                val currentParameter = getParameterUseCase().first()
                val updatedParameter = currentParameter.copy(
                    raisonsociale = form.companyName.trim(),
                    entreprisetype = form.companyType,
                    ifu = form.ifu.trim().takeIf { it.isNotBlank() },
                    adresse = form.address.trim().takeIf { it.isNotBlank() },
                    telephone = form.phone.trim().takeIf { it.isNotBlank() },
                    email = form.email.trim().takeIf { it.isNotBlank() },
                    website = form.website.trim().takeIf { it.isNotBlank() },
                    devise = form.devise.trim().uppercase(),
                    tva = form.tva.toDoubleOrNull() ?: currentParameter.tva
                )
                val tvaValue = form.tva.trim().takeIf { it.isNotBlank() }

                when (val result = updateBusinessInfoUseCase(updatedParameter, uri, tvaValue)) {
                    is Result.Success -> {
                        _parameterState.value = result.data
                        _updateBusinessUiState.value = FormUIState.Success(Unit)
                    }

                    is Result.Error -> {
                        _updateBusinessUiState.value = FormUIState.Error(
                            result.exception.message ?: "Error when updating business info"
                        )
                    }

                    else -> {}
                }
            } catch (e: Exception) {
                Log.e("UpdateBusinessVM", "Error updating business info", e)
                _updateBusinessUiState.value = FormUIState.Error(
                    e.message ?: "An unexpected error occurred"
                )
            }
        }
    }

    fun resetFlow() {
        _updateBusinessUiState.value = FormUIState.Idle
    }

    fun deleteImageFromCache(context: Context, filename: String) {
        UploadUtility.deleteImageFromCache(context, filename)
    }
}

data class UpdateBusinessFormData(
    val companyName: String = "",
    val companyType: Int = -1,
    val email: String = "",
    val address: String = "",
    val phone: String = "",
    val ifu: String = "",
    val website: String = "",
    val devise: String = "",
    val tva: String = ""
)
