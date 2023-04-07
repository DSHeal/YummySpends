package com.dsheal.yummyspends.data.repositories

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.dsheal.yummyspends.data.database.AppDatabase
import com.dsheal.yummyspends.data.mappers.SpendingsMapper
import com.dsheal.yummyspends.domain.models.spendings.Category
import com.dsheal.yummyspends.domain.models.spendings.SingleSpendingModel
import com.dsheal.yummyspends.domain.repositories.SpendingsRepository
import com.dsheal.yummyspends.presentation.base.BaseViewModel.*
import com.dsheal.yummyspends.presentation.ui.activities.MainActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import java.lang.reflect.Type
import javax.inject.Inject

class SpendinsRepositoryImpl @Inject constructor(
    private val database: AppDatabase,
    private val spendingsMapper: SpendingsMapper,
    private val firebaseDatabase: FirebaseDatabase,
    private val sharedPreferences: SharedPreferences
) : SpendingsRepository {

    companion object {
        const val TAG = "SpendingsRepImpl"
    }

    val myRef = firebaseDatabase.getReference("items")

    override suspend fun getCategoriesListFromFirebase(): Flow<State<ArrayList<String>>> =
        callbackFlow {
            firebaseDatabase.getReference("categories").get()
                .addOnCompleteListener { task ->
                    val response = if (task.isSuccessful) {
                        val data = task.result.getValue<ArrayList<String>>()!!
                        Log.i("CATEGORIES_FROM_FB", data.toString())
                        State.Success<ArrayList<String>>(data)
                    } else {
                        State.Failure(errorMessage = task.exception?.localizedMessage)
                    }
                    trySend(response).isSuccess
                }

            awaitClose {
                close()
            }
        }

//TODO do we need shared prefs? Or can we drop them?
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
        val categoriesAlreadyAdded = Gson().fromJson<List<String>>(
            sharedPreferences.getString("categories", ""),
            object : TypeToken<ArrayList<String?>?>() {}.type
        ).orEmpty()

        val editor = sharedPreferences.edit()

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
                if (innerMap.key == "spendingCategory") {
                    category = innerMap.value as String
                    val listToSave = categoriesAlreadyAdded.plus(listOf(category))
                    val json = Gson().toJson(listToSave)
                    editor.putString("categories", json)
                    editor.apply()
                }
                Log.i("CATEGORY", category)
            }
            val spending = SingleSpendingModel(
                id = idFromFb, spendingName = name,
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

    override fun sendDataToFirebaseDb(spending: SingleSpendingModel): String? {
        val key = myRef.push().key
        if (key == null) {
            Log.w(TAG, "Couldn't get push key for posts")
        }
        myRef.child(key!!).setValue(spending)
        sendCategoriesFromSharedPrefsToRemoteDb()
        return key
    }

    override fun sendCategoriesToRemoteDb(categories: ArrayList<String>) {
        firebaseDatabase.reference.child("categories").setValue(categories)
    }

    fun sendCategoriesFromSharedPrefsToRemoteDb() {

        val gson = Gson()

        val json = sharedPreferences.getString("categories", "")

        val type: Type = object : TypeToken<ArrayList<String?>?>() {}.type
        val categories =
            gson.fromJson<Any>(json, type)

        //here should be the same trick as with separate spending

        //TODO need to write check for identity 0f two lists, and if they are different -
        // only then send data to remote db so not to send data each time

        firebaseDatabase.reference.child("categories").setValue(categories)
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