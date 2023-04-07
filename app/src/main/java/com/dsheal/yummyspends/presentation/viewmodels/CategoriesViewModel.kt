package com.dsheal.yummyspends.presentation.viewmodels

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.dsheal.yummyspends.data.repositories.SpendinsRepositoryImpl
import com.dsheal.yummyspends.domain.repositories.SpendingsRepository
import com.dsheal.yummyspends.presentation.base.BaseViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoriesViewModel @Inject constructor(
    private val spendingsRepository: SpendingsRepository,
    private val sharedPreferences: SharedPreferences
) :
    BaseViewModel() {

    companion object {
        const val LOG_TAG = "CategoriesVM"
    }

    private var categoriesLiveData = MutableLiveData<State<List<String>>>()
    val categories = categoriesLiveData

    init {
        getCategoriesFromRemoteDb()
    }

    fun sendCategoriesListToRemoteDb(list: ArrayList<String>) {
        viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, throwable ->
            Log.d(LOG_TAG, throwable.message ?: "")
            categoriesLiveData.postValue(State.Failure(throwable))
        }) {
            spendingsRepository.sendCategoriesToRemoteDb(list)
        }
    }

    fun getCategoriesFromRemoteDb() {
        viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, throwable ->
            Log.d(LOG_TAG, throwable.message ?: "")
            categoriesLiveData.postValue(State.Failure(throwable))
        }) {
            val categoriesFlow = spendingsRepository.getCategoriesListFromFirebase()
            categoriesFlow.collect { state ->
                when (state) {
                    is State.Success -> {
                        Log.i("Categories from Db", state.toString())
                        categoriesLiveData.postValue(State.Success(state.data))

                    }
                    is State.Failure -> {
                        Log.i(SpendinsRepositoryImpl.TAG, state.error!!.toString())
                    }
                    is State.Loading -> {

                    }
                }
            }
        }
    }

    fun getCategoriesFromSharedPrefs() {
        val categoriesFromPrefs = Gson().fromJson<List<String>>(
            sharedPreferences.getString("categories", ""),
            object : TypeToken<ArrayList<String?>?>() {}.type
        ).orEmpty()
        categoriesLiveData.postValue(State.Success(categoriesFromPrefs))
    }
}