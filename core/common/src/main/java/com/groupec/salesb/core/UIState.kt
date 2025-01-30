package com.groupec.salesb.core

sealed class UIState<out T> {
    data object Loading : UIState<Nothing>()
    data class Error(val message: String) : UIState<Nothing>()
    data class Success<T>(val data: T) : UIState<T>()
}

sealed class FormUIState<out T> {
    data object Idle : FormUIState<Nothing>()
    data object Loading : FormUIState<Nothing>()
    data class Error(val message: String) : FormUIState<Nothing>()
    data class Success<T>(val data: T) : FormUIState<T>()
}
