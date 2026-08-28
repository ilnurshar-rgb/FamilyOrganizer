package com.family.organizer.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalDao {

    @Query("SELECT * FROM goals ORDER BY title ASC")
    fun observeAll(): Flow<List<Goal>>

    @Insert
    suspend fun insert(goal: Goal)

    @Update
    suspend fun update(goal: Goal)

    @Upsert
    suspend fun upsert(goal: Goal)

    @Query("DELETE FROM goals WHERE id = :id")
    suspend fun deleteById(id: String)
}
