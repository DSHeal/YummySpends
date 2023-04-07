package com.dsheal.yummyspends.domain.models.history

sealed class HistoryDataWrapper {

    data class CategoryTableTitle(val categoryTitle: String, val categoryPrice: Int): HistoryDataWrapper()

    data class CategoryTableItem(val itemName: String, val itemPrice: Int): HistoryDataWrapper()
}