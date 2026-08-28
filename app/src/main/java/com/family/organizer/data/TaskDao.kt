package com.family.organizer.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Query("SELECT * FROM tasks ORDER BY isDone ASC, createdAt DESC")
    fun observeAll(): Flow<List<TaskItem>>

    @Insert
    suspend fun insert(task: TaskItem)

    @Update
    suspend fun update(task: TaskItem)

    @Upsert
    suspend fun upsert(task: TaskItem)

    @Query("DELETE FROM tasks WHERE id = :id")
    suspend fun deleteById(id: String)
}
