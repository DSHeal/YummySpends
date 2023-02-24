package com.dsheal.yummyspends.presentation.base

import androidx.lifecycle.ViewModel

abstract class BaseViewModel : ViewModel() {

    sealed class State<T> {
        data class Loading<T>(val isLoading: Boolean) : State<T>()
        data class Success<T>(val data: T) : State<T>()
        data class Failure<T>(val error: Throwable? = null, val errorMessage: String? = null) : State<T>()
    }
}
sealed class Resource<T>(val data: T? = null, val error: String? = null) {
    class Success<T>(data: T) : Resource<T>(data = data)
    class Error<T>(error: String, data: T? = null) : Resource<T>(data = data, error = error)
}