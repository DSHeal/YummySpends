package com.dsheal.yummyspends.domain.interactors

import com.dsheal.yummyspends.R
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import javax.inject.Inject

class RemoteConfigInteractorImpl @Inject constructor() : RemoteConfigInteractor {

    private val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig

    init {
        initRemoteConfig()
    }

    private fun initRemoteConfig() {
        remoteConfig.setDefaultsAsync(R.xml.remote_config)
        remoteConfig.fetchAndActivate()
    }


    override fun getRemoteConfig(): FirebaseRemoteConfig = remoteConfig
}