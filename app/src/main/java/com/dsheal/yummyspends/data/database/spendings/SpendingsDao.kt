package com.dsheal.yummyspends.data.database.spendings

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
abstract class SpendingsDao {

    @Query(QUERY_GET_ALL_SPENDINGS)
   abstract fun listenAllSpendings(): List<SpendingsEntity>

    @Query(QUERY_SPENDING_BY_ID)
    abstract fun listenSpendingsById(id: Int): SpendingsEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertAllSpendings(spendingsEntityList: List<SpendingsEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertOneSpending(spending: SpendingsEntity)

    @Query(QUERY_DELETE_SPENDING_BY_ID)
    abstract fun deleteSpendingById(id: Int)

    @Query(QUERY_DELETE_ALL_SPENDINGS)
    abstract fun deleteSpendingsTable()

    @Query(QUERY_GET_SPENDINGS_BY_DATE)
    abstract fun getSpendingsByDate(date: String): List<SpendingsEntity>

    companion object {
        private const val QUERY_SPENDING_BY_ID = """
            SELECT *
            FROM ${SpendingsEntity.TABLE_NAME}
            WHERE ${SpendingsEntity.COLUMN_SPENDING_ID} = :id
        """

        private const val QUERY_DELETE_SPENDING_BY_ID = """
            DELETE 
            FROM ${SpendingsEntity.TABLE_NAME}
            WHERE ${SpendingsEntity.COLUMN_SPENDING_ID} = :id
        """

        private const val QUERY_GET_ALL_SPENDINGS = """
            SELECT *
            FROM ${SpendingsEntity.TABLE_NAME}
        """

        private const val QUERY_DELETE_ALL_SPENDINGS = """
                DELETE
                FROM ${SpendingsEntity.TABLE_NAME}
                """

        private const val QUERY_GET_SPENDINGS_BY_DATE = """
            SELECT * 
            FROM ${SpendingsEntity.TABLE_NAME} 
            WHERE ${SpendingsEntity.COLUMN_PURCHASE_DATE} = :date
        """
    }

    //может, добавить еще одну таблицу по категориям?
//    @Query(QUERY_SPENDINGS_BY_CATEGORY_ID)
//    fun getSpendingsByCategory(categoryId: Long?): Flowable<List<SpendingsEntity>>
// в аксессуарах ауди есть категории AccessoriesCategory для примера
}