package com.dsheal.yummyspends.common

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class YummySpendsApp: Application() {

    override fun onCreate() {
        super.onCreate()
    }
}