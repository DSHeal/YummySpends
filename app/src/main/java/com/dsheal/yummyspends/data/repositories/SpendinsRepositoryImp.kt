package com.dsheal.yummyspends.data.repositories

import android.util.Log
import com.dsheal.yummyspends.data.database.AppDatabase
import com.dsheal.yummyspends.data.mappers.SpendingsMapper
import com.dsheal.yummyspends.domain.models.spendings.SingleSpendingModel
import com.dsheal.yummyspends.domain.repositories.SpendingsRepository
import com.dsheal.yummyspends.presentation.base.BaseViewModel.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class SpendinsRepositoryImpl @Inject constructor(
    private val database: AppDatabase,
    private val spendingsMapper: SpendingsMapper,
    private val firebaseDatabase: FirebaseDatabase
) : SpendingsRepository {

    companion object {
        const val TAG = "SpendingsRepImpl"
    }

    val myRef = firebaseDatabase.getReference("items")

    override suspend fun getAllDataFromFirebaseDb(): Flow<State<Map<String, Any>>> = callbackFlow {
        myRef.get()
            .addOnCompleteListener { task ->
                val response = if (task.isSuccessful) {
                    val data = task.result.getValue<Map<String, HashMap<String, Any>>>()!!
                    Log.i("FROM_FB", data.toString())
                    launch {
                        saveDataFromRemoteSourceInLocalDb(data)
                    }

                    data.forEach {
                        val key = it.key
                        val value = it.value
                        val cl = value.javaClass
                        Log.i("FROM MAP", "КЛЮЧ = $key, ЗНАЧЕНИЕ = $value, КЛАСС = $cl")
                    }
                    State.Success<Map<String, Any>>(data)
                } else {
                    State.Failure(errorMessage = task.exception?.localizedMessage)
                }
                trySend(response).isSuccess
            }

        awaitClose {
            close()
        }
    }

    fun saveDataFromRemoteSourceInLocalDb(map: Map<String, HashMap<String, Any>>) {
        map.forEach {
            val idFromFb = it.key
            Log.i("ID_FROM_FB", idFromFb)
            val value = it.value
            Log.i("VALUE_FROM_FB", value.toString())
            var date = ""
            var name = ""
            var price: Long = 0
            var category = ""
            value.forEach { innerMap ->
                if (innerMap.key == "purchaseDate") date = innerMap.value as String
                Log.i("DATE", date)
                if (innerMap.key == "spendingName") name = innerMap.value as String
                Log.i("NAME", name)
                if (innerMap.key == "spendingPrice") price = innerMap.value as Long
                Log.i("PRICE", price.toString())
                if (innerMap.key == "spendingCategory") category = innerMap.value as String
                Log.i("CATEGORY", category)
            }
            val spending = SingleSpendingModel(
                id = idFromFb, spendingName = name.toString(),
                spendingPrice = price.toInt(), spendingCategory = category, purchaseDate = date
            )
            Log.i("SPENDING", spending.toString())

            saveSpendingsInDatabase(spending)
        }
    }

    override fun getSpendingByIdFromRemoteDb(id: String): Flow<State<Map<String, Any>>> {
        return callbackFlow {
            myRef.child(id).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val oneSpending = snapshot.getValue<Map<String, Any>>()!!
                    trySend(State.Success<Map<String, Any>>(oneSpending)).isSuccess
                }

                override fun onCancelled(error: DatabaseError) {
                    trySend(State.Failure(errorMessage = error.message)).isFailure
                }
            })

            awaitClose {
                close()
            }
        }
    }

    //TODO probably we don't need it
    override fun listenDataFromFirebaseDbInRealTime(): Flow<State<List<SingleSpendingModel>>> =
        callbackFlow {
            myRef
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val spendings = snapshot.getValue<List<SingleSpendingModel>>()!!
                        trySend(State.Success<List<SingleSpendingModel>>(spendings)).isSuccess
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.w("spends", "Failed to read value.")
                        trySend(State.Failure<List<SingleSpendingModel>>(errorMessage = error.message)).isFailure
                    }
                })
        }

    override fun sendDataToFirebaseDb(spending: SingleSpendingModel): String? {
        val key = myRef.push().key
        if (key == null) {
            Log.w(TAG, "Couldn't get push key for posts")
        }
        myRef.child(key!!).setValue(spending)
        return key
    }

    override fun saveSpendingsInDatabase(spending: SingleSpendingModel) {
        database.spendingsDao()
            .insertOneSpending(spendingsMapper.mapSingleSpendingModelToEntity(spending))
    }

    override fun listenSpendingsListFromDatabase(): List<SingleSpendingModel> {
        return spendingsMapper.mapSpendingsEntityListToModel(
            database.spendingsDao().listenAllSpendings()
        )
    }

    override fun deleteAllSpendingsFromDB() {
        database.spendingsDao().deleteSpendingsTable()
    }

    override fun listenSpendingsByDate(date: String): List<SingleSpendingModel> {
        return spendingsMapper.mapSpendingsEntityListToModel(
            database.spendingsDao().getSpendingsByDate(date)
        )
    }

    override fun getTableInfo(): SingleSpendingModel {
        TODO("Not yet implemented")
    }
}