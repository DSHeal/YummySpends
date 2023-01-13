package com.dsheal.yummyspends.domain.models.spendings

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SingleSpendingModel(
//    val userName: String = "",
    val id: String = "",
    val spendingName: String = "",
    val spendingPrice: Int,
    val spendingCategory: String = "",
    val purchaseDate: String = ""
): Parcelable

