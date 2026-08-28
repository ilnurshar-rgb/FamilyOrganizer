package com.family.organizer.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {

    @Query("SELECT * FROM categories WHERE type = :type ORDER BY sortOrder ASC, id ASC")
    fun observeByType(type: String): Flow<List<Category>>

    @Query("SELECT * FROM categories ORDER BY sortOrder ASC, id ASC")
    fun observeAll(): Flow<List<Category>>

    @Insert
    suspend fun insert(category: Category)

    @Insert
    suspend fun insertAll(categories: List<Category>)

    @Upsert
    suspend fun upsert(category: Category)

    @Delete
    suspend fun delete(category: Category)

    @Query("DELETE FROM categories WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("SELECT COUNT(*) FROM categories")
    suspend fun count(): Int
}
