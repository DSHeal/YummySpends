package com.dsheal.yummyspends.presentation.viewmodels

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.dsheal.yummyspends.data.repositories.SpendinsRepositoryImpl
import com.dsheal.yummyspends.data.repositories.SpendinsRepositoryImpl.Companion.TAG
import com.dsheal.yummyspends.domain.models.spendings.SingleSpendingModel
import com.dsheal.yummyspends.domain.repositories.SpendingsRepository
import com.dsheal.yummyspends.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AllSpendingsFieldViewModel @Inject constructor(
    private val spendingsRepository: SpendingsRepository,
    private val sharedPreferences: SharedPreferences
) : BaseViewModel() {

    private var spendingLiveData = MutableLiveData<State<List<SingleSpendingModel>>>()

    val spending = spendingLiveData

    init {
        getAllDataFromFirebaseDB()
    }

    fun sendDataToRemoteDb(
        spending: SingleSpendingModel
    ) {
        viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, throwable ->
            Log.d("AddNewSpendVM", throwable.message ?: "")
            spendingLiveData.postValue(State.Failure(throwable))
        }) {
            getSpendingByIdFromRemoteDbAndSaveToLocalDb(
                spendingsRepository.sendDataToFirebaseDb(
                    spending
                )
            )
        }
    }

    fun getCategoriesFromRemoteDbAndWriteToSharedPrefs() {
        viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, throwable ->
            Log.d("AllSpendVM", throwable.message ?: "")
//            categoriesLiveData.postValue(State.Failure(throwable))
        }) {
            val categoriesFlow = spendingsRepository.getCategoriesListFromFirebase()
            categoriesFlow.collect { state ->
                when (state) {
                    is State.Success -> {
                        Log.i("Categories from Db", state.toString())
//                        categoriesLiveData.postValue(State.Success(state.data))
sharedPreferences.edit()
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

    suspend fun getSpendingByIdFromRemoteDbAndSaveToLocalDb(id: String?) {
        var date = ""
        var name = ""
        var price: Long = 0
        var category = ""
        spendingsRepository.getSpendingByIdFromRemoteDb(id!!).collect { state ->
            when (state) {
                is State.Success -> {
                    state.data.forEach { spending ->
                        if (spending.key == "purchaseDate") date = spending.value as String
                        Log.i("DATE", date)
                        if (spending.key == "spendingName") name = spending.value as String
                        Log.i("NAME", name)
                        if (spending.key == "spendingPrice") price = spending.value as Long
                        Log.i("PRICE", price.toString())
                        if (spending.key == "spendingCategory") category = spending.value as String
                        Log.i("CATEGORY", category)
                    }
                    val spendingFromRemote = SingleSpendingModel(
                        id = id,
                        spendingName = name,
                        spendingPrice = price.toInt(),
                        spendingCategory = category,
                        purchaseDate = date
                    )
                    saveSpendingInDb(spendingFromRemote)
                }
                is State.Failure -> {
                    Log.i(TAG, state.error!!.toString())
                }
                is State.Loading -> {}
            }
        }
    }

    fun saveSpendingInDb(
        spending: SingleSpendingModel
    ) {
        viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, throwable ->
            Log.d("AddNewSpendVM", throwable.message ?: "")
            spendingLiveData.postValue(State.Failure(throwable))
        }) {
            spendingsRepository.saveSpendingsInDatabase(
                spending
            )
        }
    }

    fun getAllDataFromFirebaseDB() {
        viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, throwable ->
            Log.d("AddNewSpendVM", throwable.message ?: "")
            spendingLiveData.postValue(State.Failure(throwable))
        }) {
            val spendingsFromFbFlow = spendingsRepository.getAllDataFromFirebaseDb()
            spendingsFromFbFlow.collect { state ->
                when (state) {
                    is State.Success -> {
                        state.data.forEach { spending ->
//                            saveSpendingInDb(spending)
                        }
                    }
                    is State.Failure -> {
                        Log.i(TAG, state.error!!.toString())
                    }
                    is State.Loading -> {

                    }
                }
            }
        }
    }

    fun listenAllSpendingsFromDb() {
        viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, throwable ->
            Log.d("AddNewSpendVM", throwable.message ?: "")
            spendingLiveData.postValue(State.Failure(throwable))
        }) {
            spendingLiveData.postValue(State.Success(spendingsRepository.listenSpendingsListFromDatabase()))
        }
    }

    fun listenSpendingsByDate(date: String) {
        viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, throwable ->
            spendingLiveData.postValue(State.Failure(throwable))
        }) {
            spendingLiveData.postValue(State.Success(spendingsRepository.listenSpendingsByDate(date)))
        }
    }

    fun deleteAllSpendingsFromDb() {
        viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, throwable ->
            spendingLiveData.postValue(State.Failure(throwable))
        }) {
            spendingsRepository.deleteAllSpendingsFromDB()
        }
    }

}