package com.dsheal.yummyspends.common

import android.content.Context

class Utils {

    fun dpToPx(dp: Int, context: Context): Int {
        return (dp * context.resources.displayMetrics.density).toInt()
    }

    fun formatDateToDateMonthYear() {

    }
}