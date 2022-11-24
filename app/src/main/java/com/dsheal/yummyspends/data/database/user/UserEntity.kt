package com.dsheal.yummyspends.data.database.user

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(
    tableName = UserEntity.TABLE_NAME,
    primaryKeys = [
        UserEntity.COLUMN_USER_ID
    ]
)

class UserEntity(
    @ColumnInfo(name = COLUMN_USER_ID)
    val userId: Int,
    @ColumnInfo(name = COLUMN_USER_NAME)
    val userName: String
) {
    companion object {
        const val TABLE_NAME = "users"
        const val COLUMN_USER_ID = "user_id"
        const val COLUMN_USER_NAME = "user_name"
    }
}