package com.dsheal.yummyspends.presentation.base

import androidx.lifecycle.ViewModel

abstract class BaseViewModel : ViewModel() {

    sealed class State<T> {
        data class Loading<T>(val isLoading: Boolean) : State<T>()
        data class Success<T>(val data: T) : State<T>()
        data class Failure<T>(val error: Throwable) : State<T>()
    }
}
