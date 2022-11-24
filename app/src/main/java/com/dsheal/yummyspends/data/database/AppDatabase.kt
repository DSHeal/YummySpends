package com.dsheal.yummyspends.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.dsheal.yummyspends.data.database.spendings.SpendingsDao
import com.dsheal.yummyspends.data.database.spendings.SpendingsEntity

@Database(version = 1, exportSchema = true, entities = arrayOf(
    SpendingsEntity::class
))
abstract class AppDatabase: RoomDatabase() {
    abstract fun spendingsDao(): SpendingsDao
}




