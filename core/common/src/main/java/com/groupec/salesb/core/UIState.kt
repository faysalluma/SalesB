package com.groupec.salesb.core

sealed class UIState<out T> {
    data object Loading : UIState<Nothing>()
    data class Error(val message: String) : UIState<Nothing>()
    data class Success<T>(val data: T) : UIState<T>()
}
