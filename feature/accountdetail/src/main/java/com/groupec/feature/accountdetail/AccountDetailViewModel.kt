package com.groupec.feature.accountdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.groupec.salesb.core.FormUIState
import com.groupec.salesb.core.Result
import com.groupec.salesb.core.domain.user.SaveUserUseCase
import com.groupec.salesb.core.model.data.User
import com.groupec.salesb.core.ui.UserDataForm
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class AccountDetailViewModel @Inject constructor(
    private val saveUserUseCase: SaveUserUseCase
) : ViewModel() {

    private val _addUserUiState = MutableStateFlow<FormUIState<*>>(FormUIState.Idle)
    val addUserUiState : StateFlow<FormUIState<*>> = _addUserUiState.asStateFlow()

    fun addUser(user: UserDataForm) {
        _addUserUiState.value = FormUIState.Loading
        viewModelScope.launch {
            val userModel = User(
                id = user.id.takeIf { it.isNotEmpty() }?.toInt(),
                nomprenom = user.nomprenom,
                adresse = user.adresse,
                tel = user.tel,
                email = user.email,
                password = user.password,
                privilege = user.privilege.joinToString(separator = ","),
                actif = user.actif == 1,
                firstlogin = true,
                synchronised = false,
                langMessageEn = Locale.getDefault().language == "en"
            )

            when (val result = saveUserUseCase(userModel)) {
                is Result.Success -> {
                    _addUserUiState.value = FormUIState.Success(Unit)
                }

                is Result.Error -> {
                    _addUserUiState.value = FormUIState.Error(result.exception.message ?: "Error when adding user")
                }
                else -> {}
            }
        }
    }

    fun resetFlow() {
        _addUserUiState.value = FormUIState.Idle
    }
}
