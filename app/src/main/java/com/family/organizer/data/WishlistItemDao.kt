package com.family.organizer.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface WishlistItemDao {

    @Query("SELECT * FROM wishlist_items ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<WishlistItem>>

    @Insert
    suspend fun insert(item: WishlistItem)

    @Update
    suspend fun update(item: WishlistItem)

    @Upsert
    suspend fun upsert(item: WishlistItem)

    @Query("DELETE FROM wishlist_items WHERE id = :id")
    suspend fun deleteById(id: String)
}
