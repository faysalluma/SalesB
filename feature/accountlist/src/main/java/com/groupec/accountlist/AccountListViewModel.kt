package com.groupec.accountlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.groupec.salesb.core.FormUIState
import com.groupec.salesb.core.domain.user.DeleteUserUseCase
import com.groupec.salesb.core.domain.user.GetUserUseCase
import com.groupec.salesb.core.domain.user.SaveUserUseCase
import com.groupec.salesb.core.model.data.User
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
import com.groupec.salesb.core.Result

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class AccountListViewModel @Inject constructor(
    private val getUserUseCase: GetUserUseCase,
    private val deleteUserUseCase: DeleteUserUseCase
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isSearching = MutableStateFlow(false) // État de recherche
    val isSearching: StateFlow<Boolean> = _isSearching

    private val _deleteUserUiState = MutableStateFlow<FormUIState<*>>(FormUIState.Idle)
    val deleteUserUiState: StateFlow<FormUIState<*>> = _deleteUserUiState.asStateFlow()

    val pagedUsers: Flow<PagingData<User>> = _searchQuery
        .flatMapLatest { query ->
            _isSearching.value = true // Indique qu'une recherche commence
            getUserUseCase(query)
                .onCompletion {
                    _isSearching.value = false
                } // Recherche terminée
        }
        .cachedIn(viewModelScope) // Cache les données pour le cycle de vie de l'UI

    fun updateSearchQuery(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun deleteUser(id: Int) {
        _deleteUserUiState.value = FormUIState.Loading
        viewModelScope.launch {
            when (val result = deleteUserUseCase(id)) {
                is Result.Success -> {
                    _deleteUserUiState.value = FormUIState.Success(Unit)
                }

                is Result.Error -> {
                    _deleteUserUiState.value =
                        FormUIState.Error(result.exception.message ?: "Error when deleting user")
                }

                else -> {}
            }
        }
    }

    fun resetFlow() {
        _deleteUserUiState.value = FormUIState.Idle
    }
}
