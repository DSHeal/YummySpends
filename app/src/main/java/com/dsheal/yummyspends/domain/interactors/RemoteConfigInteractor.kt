package com.dsheal.yummyspends.domain.interactors

import com.google.firebase.remoteconfig.FirebaseRemoteConfig

interface RemoteConfigInteractor {
    fun getRemoteConfig(): FirebaseRemoteConfig
}