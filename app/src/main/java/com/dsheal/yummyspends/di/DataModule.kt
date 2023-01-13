package com.dsheal.yummyspends.di

import android.content.Context
import androidx.room.Room
import com.dsheal.yummyspends.common.Constants
import com.dsheal.yummyspends.data.database.AppDatabase
import com.dsheal.yummyspends.data.database.spendings.SpendingsDao
import com.dsheal.yummyspends.data.repositories.SpendinsRepositoryImpl
import com.dsheal.yummyspends.domain.interactors.RemoteConfigInteractor
import com.dsheal.yummyspends.domain.interactors.RemoteConfigInteractorImpl
import com.dsheal.yummyspends.domain.repositories.SpendingsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataModule {

    @Provides
    @Singleton
    fun provideSpendingsRepository(spendingsRepositoryImpl: SpendinsRepositoryImpl): SpendingsRepository {
        return spendingsRepositoryImpl
    }

    @Provides
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, Constants.APP_DATABASE)
            .build()
    }

    @Provides
    fun providependingsDao(spendingsDatabase: AppDatabase): SpendingsDao {
        return spendingsDatabase.spendingsDao()
    }

    @Provides
    @Singleton
    fun provideRemoteConfig(): RemoteConfigInteractor = RemoteConfigInteractorImpl()
}