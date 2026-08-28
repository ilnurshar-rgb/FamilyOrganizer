package com.family.organizer.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface SavingsAccountDao {

    @Query("SELECT * FROM savings_accounts ORDER BY title ASC")
    fun observeAll(): Flow<List<SavingsAccount>>

    @Insert
    suspend fun insert(account: SavingsAccount)

    @Update
    suspend fun update(account: SavingsAccount)

    @Upsert
    suspend fun upsert(account: SavingsAccount)

    @Query("DELETE FROM savings_accounts WHERE id = :id")
    suspend fun deleteById(id: String)
}
