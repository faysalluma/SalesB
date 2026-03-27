package com.groupec.accountlist

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.groupec.salesb.core.FormUIState
import com.groupec.salesb.core.Result
import com.groupec.salesb.core.currentDateString
import com.groupec.salesb.core.domain.parameter.GetParameterUseCase
import com.groupec.salesb.core.domain.user.DeleteUserUseCase
import com.groupec.salesb.core.domain.user.GenerateUserListExcelUseCase
import com.groupec.salesb.core.domain.user.GenerateUserListPdfUseCase
import com.groupec.salesb.core.domain.user.GetAllUsersUseCase
import com.groupec.salesb.core.domain.user.GetUserUseCase
import com.groupec.salesb.core.domain.user.GetUserStoreUseCase
import com.groupec.salesb.core.model.data.Parameter
import com.groupec.salesb.core.model.data.User
import com.groupec.salesb.core.model.data.UserStore
import com.groupec.salesb.core.saveExcelToDownloads
import com.groupec.salesb.core.savePdfToDownloads
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class AccountListViewModel @Inject constructor(
    private val getUserUseCase: GetUserUseCase,
    private val deleteUserUseCase: DeleteUserUseCase,
    private val getAllUsersUseCase: GetAllUsersUseCase,
    private val getUserStoreUseCase: GetUserStoreUseCase,
    private val generateUserListPdfUseCase: GenerateUserListPdfUseCase,
    private val generateUserListExcelUseCase: GenerateUserListExcelUseCase,
    private val getParameterUseCase: GetParameterUseCase
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isSearching = MutableStateFlow(false) // État de recherche
    val isSearching: StateFlow<Boolean> = _isSearching

    private val _totalUsersCount = MutableStateFlow(0)
    val totalUsersCount: StateFlow<Int> = _totalUsersCount.asStateFlow()

    private val _deleteUserUiState = MutableStateFlow<FormUIState<*>>(FormUIState.Idle)
    val deleteUserUiState: StateFlow<FormUIState<*>> = _deleteUserUiState.asStateFlow()

    private val _exportPdfUiState = MutableStateFlow<FormUIState<File>>(FormUIState.Idle)
    val exportPdfUiState: StateFlow<FormUIState<File>> = _exportPdfUiState.asStateFlow()

    private val _exportExcelUiState = MutableStateFlow<FormUIState<File>>(FormUIState.Idle)
    val exportExcelUiState: StateFlow<FormUIState<File>> = _exportExcelUiState.asStateFlow()
    private val _parameterState = MutableStateFlow(Parameter())
    val parameterState: StateFlow<Parameter> = _parameterState.asStateFlow()
    private val _userStoreState = MutableStateFlow(UserStore())
    val userStoreState: StateFlow<UserStore> = _userStoreState.asStateFlow()

    init {
        observeParameters()
        observeUserStore()
        observeTotalUsersCount()
    }

    private fun observeParameters() {
        viewModelScope.launch {
            getParameterUseCase().collectLatest { parameter ->
                _parameterState.value = parameter
            }
        }
    }

    private fun observeUserStore() {
        viewModelScope.launch {
            getUserStoreUseCase().collectLatest { userStore ->
                _userStoreState.value = userStore
            }
        }
    }

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

    private fun observeTotalUsersCount() {
        viewModelScope.launch {
            _searchQuery.collectLatest { query ->
                _totalUsersCount.value = when (val result = getAllUsersUseCase(query)) {
                    is Result.Success -> result.data.size
                    else -> 0
                }
            }
        }
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

    fun exportUsersToPdf(activityContext: Context) {
        viewModelScope.launch {
            _exportPdfUiState.value = FormUIState.Loading
            when (val result = getAllUsersUseCase(_searchQuery.value)) {
                is Result.Success -> {
                    val users = result.data
                    if (users.isEmpty()) {
                        _exportPdfUiState.value = FormUIState.Error(
                            activityContext.getString(R.string.no_users_to_export)
                        )
                        return@launch
                    }
                    try {
                        val logoUrl = getParameterUseCase().first().logo
                        val pdfBytes = withContext(Dispatchers.Default) {
                            generateUserListPdfUseCase(
                                activityContext,
                                users,
                                _searchQuery.value,
                                logoUrl
                            )
                        }
                        val file = withContext(Dispatchers.IO) {
                            activityContext.savePdfToDownloads(
                                pdfBytes,
                                "users-${currentDateString("yyyy-MM-dd-HHmmss")}.pdf"
                            )
                        }
                        _exportPdfUiState.value = FormUIState.Success(file)
                    } catch (exception: Exception) {
                        _exportPdfUiState.value = FormUIState.Error(
                            exception.message ?: activityContext.getString(R.string.export_pdf_failed)
                        )
                    }
                }

                is Result.Error -> {
                    _exportPdfUiState.value = FormUIState.Error(
                        result.exception.message ?: activityContext.getString(R.string.export_pdf_failed)
                    )
                }

                else -> {}
            }
        }
    }

    fun exportUsersToExcel(activityContext: Context) {
        viewModelScope.launch {
            _exportExcelUiState.value = FormUIState.Loading
            when (val result = getAllUsersUseCase(_searchQuery.value)) {
                is Result.Success -> {
                    val users = result.data
                    if (users.isEmpty()) {
                        _exportExcelUiState.value = FormUIState.Error(
                            activityContext.getString(R.string.no_users_to_export)
                        )
                        return@launch
                    }
                    try {
                        val excelBytes = withContext(Dispatchers.Default) {
                            generateUserListExcelUseCase(activityContext, users, _searchQuery.value)
                        }
                        val file = withContext(Dispatchers.IO) {
                            activityContext.saveExcelToDownloads(
                                excelBytes,
                                "users-${currentDateString("yyyy-MM-dd-HHmmss")}.csv"
                            )
                        }
                        _exportExcelUiState.value = FormUIState.Success(file)
                    } catch (exception: Exception) {
                        _exportExcelUiState.value = FormUIState.Error(
                            exception.message ?: activityContext.getString(R.string.export_excel_failed)
                        )
                    }
                }

                is Result.Error -> {
                    _exportExcelUiState.value = FormUIState.Error(
                        result.exception.message ?: activityContext.getString(R.string.export_excel_failed)
                    )
                }

                else -> {}
            }
        }
    }

    fun resetExportState() {
        _exportPdfUiState.value = FormUIState.Idle
    }

    fun resetExportExcelState() {
        _exportExcelUiState.value = FormUIState.Idle
    }

    fun resetFlow() {
        _deleteUserUiState.value = FormUIState.Idle
    }
}
