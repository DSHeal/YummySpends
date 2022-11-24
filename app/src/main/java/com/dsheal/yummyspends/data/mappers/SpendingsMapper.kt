package com.dsheal.yummyspends.data.mappers

import com.dsheal.yummyspends.data.database.spendings.SpendingsEntity
import com.dsheal.yummyspends.domain.models.spendings.SingleSpendingModel
import javax.inject.Inject

class SpendingsMapper @Inject constructor() {

    fun mapSingleSpendingModelToEntity(spendingsModel: SingleSpendingModel): SpendingsEntity =
        SpendingsEntity(
            spendingId = 0,
            spendingName = spendingsModel.spendingName,
            spendingPrice = spendingsModel.spendingPrice,
            spendingCategory = spendingsModel.spendingCategory,
            purchaseDate = spendingsModel.purchaseDate
        )

    fun mapSpendingsEntityListToModel(spendingsEntity: List<SpendingsEntity>): List<SingleSpendingModel> =
        spendingsEntity.map { spending ->
            mapSpendingsEntityToModel(spending)
        }

    private fun mapSpendingsEntityToModel(spendingsEntity: SpendingsEntity): SingleSpendingModel =
        SingleSpendingModel(
            spendingName = spendingsEntity.spendingName,
            spendingPrice = spendingsEntity.spendingPrice,
            spendingCategory = spendingsEntity.spendingCategory,
            purchaseDate = spendingsEntity.purchaseDate
        )
}