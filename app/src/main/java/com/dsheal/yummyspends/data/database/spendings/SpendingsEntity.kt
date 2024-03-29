package com.dsheal.yummyspends.data.database.spendings

import androidx.room.*

@Entity(
    tableName = SpendingsEntity.TABLE_NAME,
    primaryKeys = [
        SpendingsEntity.COLUMN_SPENDING_ID]
//TODO link then to userEntity
//    foreignKeys = [
//        ForeignKey(
//            entity = UserEntity::class,
//            parentColumns = [SpendingsEntity.COLUMN_USER_NAME],
//            childColumns = [UserEntity.COLUMN_USER_NAME],
//            onDelete = ForeignKey.CASCADE
//        )]
)

class SpendingsEntity(
//TODO link then to userEntity
//    @PrimaryKey(autoGenerate = true)
//    @ColumnInfo(name = COLUMN_USER_NAME)
//    val userName: String,
    @ColumnInfo(name = COLUMN_SPENDING_ID)
    val spendingId: String,
    @ColumnInfo(name = COLUMN_ITEM_NAME)
    val spendingName: String,
    @ColumnInfo(name = COLUMN_ITEM_PRICE)
    val spendingPrice: Int,
    @ColumnInfo(name = COLUMN_ITEM_CATEGORY)
    val spendingCategory: String,
    @ColumnInfo(name = COLUMN_PURCHASE_DATE)
    val purchaseDate: String
) {
    companion object {
        const val TABLE_NAME = "spendings"
        const val COLUMN_SPENDING_ID = "spending_id"
        const val COLUMN_USER_NAME = "user_name"
        const val COLUMN_ITEM_NAME = "item_name"
        const val COLUMN_ITEM_PRICE = "item_price"
        const val COLUMN_ITEM_CATEGORY = "item_category"
        const val COLUMN_PURCHASE_DATE = "purchase_date"
    }
}
