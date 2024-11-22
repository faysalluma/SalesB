package com.groupec.salesb.ui


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.groupec.salesb.core.domain.user.GetUserStoreUseCase
import com.groupec.salesb.core.model.data.UserStore
import com.groupec.salesb.feature.loading.ConfigUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class MainViewModel @Inject constructor(
    private val getUserStoreUseCase: GetUserStoreUseCase
) : ViewModel() {

    private val _userStore = MutableStateFlow(UserStore())
    val userStore: StateFlow<UserStore> = _userStore.asStateFlow()

    init {
        viewModelScope.launch {
            _userStore.value = getUserStoreUseCase().firstOrNull() ?: UserStore()
        }
    }
}
