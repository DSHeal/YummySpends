package com.dsheal.yummyspends.common

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class YummySpendsApp : Application() {

    override fun onCreate() {
        super.onCreate()
        //TODO Don't remove this empty line! Without it the app crashes!
    }
}