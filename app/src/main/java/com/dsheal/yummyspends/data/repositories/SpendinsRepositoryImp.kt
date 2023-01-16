package com.dsheal.yummyspends.data.repositories

import com.dsheal.yummyspends.data.database.AppDatabase
import com.dsheal.yummyspends.data.mappers.SpendingsMapper
import com.dsheal.yummyspends.domain.models.spendings.SingleSpendingModel
import com.dsheal.yummyspends.domain.repositories.SpendingsRepository
import javax.inject.Inject

class SpendinsRepositoryImpl @Inject constructor(
    private val database: AppDatabase,
    private val spendingsMapper: SpendingsMapper
) : SpendingsRepository {

    override fun saveSpendingsInDatabase(spending: SingleSpendingModel) {
        database.spendingsDao().insertOneSpending(spendingsMapper.mapSingleSpendingModelToEntity(spending))
    }

    override fun listenSpendingsListFromDatabase(): List<SingleSpendingModel> {
        return spendingsMapper.mapSpendingsEntityListToModel(database.spendingsDao().listenAllSpendings())
    }

    override fun deleteAllSpendingsFromDB() {
        database.spendingsDao().deleteSpendingsTable()
    }

    override fun listenSpendingsByDate(date: String): List<SingleSpendingModel> {
        return spendingsMapper.mapSpendingsEntityListToModel(database.spendingsDao().getSpendingsByDate(date))
    }
}