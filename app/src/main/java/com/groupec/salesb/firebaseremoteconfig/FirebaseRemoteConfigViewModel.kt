package com.groupec.cleanarchitecturesampleapp.firebaseremoteconfig

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.groupec.salesb.core.firebaseremoteconfig.FirebaseRemoteConfigProvider
import com.groupec.salesb.core.firebaseremoteconfig.HomeScreenFeatureFlag
import com.groupec.salesb.core.firebaseremoteconfig.defaultValueMap
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FirebaseRemoteConfigViewModel @Inject constructor(
    private val firebaseRemoteConfigProvider: FirebaseRemoteConfigProvider,
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    init {
        viewModelScope.launch {
            fetchRemoteConfig()
        }
    }

    private suspend fun fetchRemoteConfig() {
        firebaseRemoteConfigProvider
            .configKeys(
                keyList =
                    listOf(
                        HomeScreenFeatureFlag.FORCE_UPDATE.keyName,
                        HomeScreenFeatureFlag.MESSAGE.keyName,
                        HomeScreenFeatureFlag.OTHER.keyName,
                    ),
            )
            .collectLatest { configMap ->
                _uiState.update { currentState ->
                    currentState.copy(
                        message =
                            configMap[HomeScreenFeatureFlag.MESSAGE.keyName]?.let {
                                firebaseRemoteConfigProvider.getStringFlagValue(configMap, HomeScreenFeatureFlag.MESSAGE.keyName)
                            } ?: currentState.message,

                        other =
                            configMap[HomeScreenFeatureFlag.OTHER.keyName]?.let {
                                firebaseRemoteConfigProvider.getStringFlagValue(configMap, HomeScreenFeatureFlag.OTHER.keyName)
                            } ?: currentState.other,

                        forceUpdate =
                            configMap[HomeScreenFeatureFlag.FORCE_UPDATE.keyName]?.let {
                                firebaseRemoteConfigProvider.getBooleanFlagValue(configMap, HomeScreenFeatureFlag.FORCE_UPDATE.keyName)
                            } ?: currentState.forceUpdate,
                    )
                }
            }
    }
}

data class HomeUiState(
    val forceUpdate: Boolean =
        defaultValueMap[HomeScreenFeatureFlag.FORCE_UPDATE.keyName] as Boolean,
    val message: String =
        defaultValueMap[HomeScreenFeatureFlag.MESSAGE.keyName] as String,
    val other: String =
        defaultValueMap[HomeScreenFeatureFlag.OTHER.keyName] as String,
)