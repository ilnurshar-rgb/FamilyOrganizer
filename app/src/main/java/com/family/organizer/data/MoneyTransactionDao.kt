package com.family.organizer.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface MoneyTransactionDao {

    @Query("SELECT * FROM transactions ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<MoneyTransaction>>

    @Insert
    suspend fun insert(transaction: MoneyTransaction)

    @Upsert
    suspend fun upsert(transaction: MoneyTransaction)

    @Query("DELETE FROM transactions WHERE id = :id")
    suspend fun deleteById(id: String)
}
