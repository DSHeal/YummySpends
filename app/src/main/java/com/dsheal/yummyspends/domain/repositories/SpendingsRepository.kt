package com.dsheal.yummyspends.domain.repositories

import com.dsheal.yummyspends.domain.models.spendings.SingleSpendingModel

interface SpendingsRepository {

    fun saveSpendingsInDatabase(spending: SingleSpendingModel)

    fun listenSpendingsListFromDatabase(): List<SingleSpendingModel>

    fun deleteAllSpendingsFromDB()

}