package com.dsheal.yummyspends.domain.repositories

import com.dsheal.yummyspends.domain.models.spendings.SingleSpendingModel
import com.dsheal.yummyspends.presentation.base.BaseViewModel.*
import kotlinx.coroutines.flow.Flow

interface SpendingsRepository {

    suspend fun getAllDataFromFirebaseDb(): Flow<State<Map<String, Any>>>

    fun listenDataFromFirebaseDbInRealTime(): Flow<State<List<SingleSpendingModel>>>

    fun saveSpendingsInDatabase(spending: SingleSpendingModel)

    fun getSpendingByIdFromRemoteDb(id: String): Flow<State<Map<String, Any>>>

    fun sendDataToFirebaseDb(spending: SingleSpendingModel): String?

    fun listenSpendingsListFromDatabase(): List<SingleSpendingModel>

    fun deleteAllSpendingsFromDB()

    fun listenSpendingsByDate(date: String): List<SingleSpendingModel>

    fun getTableInfo(): SingleSpendingModel

}