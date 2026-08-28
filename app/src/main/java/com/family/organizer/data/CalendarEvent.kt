package com.family.organizer.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

/**
 * Событие календаря. dateEpochDay — LocalDate.toEpochDay(), удобно
 * сравнивать/сортировать без хранения полного timestamp с часовым поясом.
 */
@Entity(tableName = "calendar_events")
data class CalendarEvent(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val title: String,
    val type: String = "family", // "family" | "birthday" | "personal" | "other"
    val dateEpochDay: Long,
    val timeLabel: String? = null,
    val allDay: Boolean = true,
    val recurrence: String = "none", // "none" | "yearly"
    val participantsLabel: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
) {
    companion object {
        fun fromFirestoreMap(id: String, data: Map<String, Any?>): CalendarEvent = CalendarEvent(
            id = id,
            title = data["title"] as? String ?: "",
            type = data["type"] as? String ?: "family",
            dateEpochDay = (data["dateEpochDay"] as? Number)?.toLong() ?: 0L,
            timeLabel = data["timeLabel"] as? String,
            allDay = data["allDay"] as? Boolean ?: true,
            recurrence = data["recurrence"] as? String ?: "none",
            participantsLabel = data["participantsLabel"] as? String,
            createdAt = (data["createdAt"] as? Number)?.toLong() ?: System.currentTimeMillis(),
        )
    }
}

fun CalendarEvent.toFirestoreMap(): Map<String, Any?> = mapOf(
    "title" to title,
    "type" to type,
    "dateEpochDay" to dateEpochDay,
    "timeLabel" to timeLabel,
    "allDay" to allDay,
    "recurrence" to recurrence,
    "participantsLabel" to participantsLabel,
    "createdAt" to createdAt,
)
