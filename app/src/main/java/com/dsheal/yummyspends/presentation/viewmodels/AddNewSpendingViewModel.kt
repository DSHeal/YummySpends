package com.dsheal.yummyspends.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.dsheal.yummyspends.data.repositories.SpendinsRepositoryImpl
import com.dsheal.yummyspends.domain.models.spendings.SingleSpendingModel
import com.dsheal.yummyspends.domain.repositories.SpendingsRepository
import com.dsheal.yummyspends.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddNewSpendingViewModel @Inject constructor(
    private val spendingsRepository: SpendingsRepository
) : BaseViewModel() {

    companion object {
        const val LOG_TAG = "AddNewSpendVM"
    }

    private var spendingLiveData = MutableLiveData<State<List<SingleSpendingModel>>>()

    val spending = spendingLiveData

    var savingInDbResult = MutableLiveData<Throwable>()

    var spendingFromFbDbWithRemoteId: SingleSpendingModel? = null

    fun sendSpendToRemoteDb(
        spending: SingleSpendingModel
    ) {
        viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, throwable ->
            Log.d(LOG_TAG, throwable.message ?: "")
            spendingLiveData.postValue(State.Failure(throwable))
        }) {
            val key = spendingsRepository.sendDataToFirebaseDb(
                spending
            )
            spendingFromFbDbWithRemoteId = getSpendingByIdFromRemoteDb(key)
        }.invokeOnCompletion {
            spendingFromFbDbWithRemoteId?.let { error -> saveSpendingInDb(error) }
        }
    }

    suspend fun getSpendingByIdFromRemoteDb(id: String?): SingleSpendingModel {
        var spend: SingleSpendingModel? = null
        spendingsRepository.getSpendingByIdFromRemoteDb(id!!).collect { state ->
            when (state) {
                is State.Success -> {
                    state.data.forEach { spending ->
                        val idFromFb = spending.key
                        Log.i("BY_ID", "ID_FROM_FB = $idFromFb")
                        val value = spending.value as HashMap<String, Any>
                        Log.i("BY_ID", value.toString())
                        var date = ""
                        var name = ""
                        var price: Long = 0
                        var category = ""
                        value.forEach { innerMap ->
                            if (innerMap.key == "purchaseDate") date = innerMap.value as String
                            Log.i("DATE", date.toString())
                            if (innerMap.key == "spendingName") name = innerMap.value as String
                            Log.i("NAME", name.toString())
                            if (innerMap.key == "spendingPrice") price = innerMap.value as Long
                            Log.i("PRICE", price.toString())
                            if (innerMap.key == "spendingCategory") category =
                                innerMap.value as String
                            Log.i("CATEGORY", category.toString())
                        }
                        val spendingFromRemote = SingleSpendingModel(
                            id = idFromFb,
                            spendingName = name.toString(),
                            spendingPrice = price.toInt(),
                            spendingCategory = category,
                            purchaseDate = date
                        )
                        spend = spendingFromRemote
                    }
                }
                is State.Failure -> {
                    Log.i(SpendinsRepositoryImpl.TAG, state.error!!.toString())
                }
                is State.Loading -> {

                }
            }
        }
        return spend!!
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

}