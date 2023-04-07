package com.dsheal.yummyspends.presentation.base

import androidx.lifecycle.ViewModel

abstract class BaseViewModel : ViewModel() {

    //TODO think probably add constructor with params like (val data: T? = null, val error: String? = null)
    sealed class State<T> {
        data class Loading<T>(val isLoading: Boolean) : State<T>()
        data class Success<T>(val data: T) : State<T>()
        data class Failure<T>(val error: Throwable? = null, val errorMessage: String? = null) : State<T>()
    }
}