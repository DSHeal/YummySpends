package com.dsheal.yummyspends.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
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

    private var spendingLiveData = MutableLiveData<State<List<SingleSpendingModel>>>()

    val spending = spendingLiveData

    var savingInDbResult = MutableLiveData<Throwable>()

    fun saveSpendingInDb(
        spendTitle: String,
        spendCost: Int,
        spendCategory: String,
        currentDate: String
    ) {
        viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, throwable ->
            Log.d("AddNewSpendVM", throwable.message ?: "")
            spendingLiveData.postValue(State.Failure(throwable))
        }) {
            spendingsRepository.saveSpendingsInDatabase(
                SingleSpendingModel(
                    spendingName = spendTitle,
                    spendingPrice = spendCost,
                    spendingCategory = spendCategory,
                    purchaseDate = currentDate
                )
            )
        }
    }

}