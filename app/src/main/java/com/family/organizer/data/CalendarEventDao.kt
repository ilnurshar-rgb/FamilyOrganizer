package com.family.organizer.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface CalendarEventDao {

    @Query("SELECT * FROM calendar_events ORDER BY dateEpochDay ASC")
    fun observeAll(): Flow<List<CalendarEvent>>

    @Insert
    suspend fun insert(event: CalendarEvent)

    @Upsert
    suspend fun upsert(event: CalendarEvent)

    @Query("DELETE FROM calendar_events WHERE id = :id")
    suspend fun deleteById(id: String)
}
