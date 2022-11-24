package com.dsheal.yummyspends.domain.models.spendings

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SingleSpendingModel(
//    val userName: String = "",
    val spendingName: String = "",
    val spendingPrice: Int,
    val spendingCategory: String = "",
    val purchaseDate: String = ""
): Parcelable

