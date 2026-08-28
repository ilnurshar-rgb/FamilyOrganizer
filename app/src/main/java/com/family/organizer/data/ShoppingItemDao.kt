package com.family.organizer.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface ShoppingItemDao {

    @Query("SELECT * FROM shopping_items ORDER BY isBought ASC, createdAt DESC")
    fun observeAll(): Flow<List<ShoppingItem>>

    @Insert
    suspend fun insert(item: ShoppingItem)

    @Update
    suspend fun update(item: ShoppingItem)

    @Upsert
    suspend fun upsert(item: ShoppingItem)

    @Query("DELETE FROM shopping_items WHERE id = :id")
    suspend fun deleteById(id: String)
}
