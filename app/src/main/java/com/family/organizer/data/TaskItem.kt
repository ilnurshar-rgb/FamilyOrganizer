package com.family.organizer.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

/**
 * Задача по дому. dueBucket — упрощение вместо точной даты выполнения
 * ("today" | "week" | "none"), повторяет группировку в макете
 * (family-app-mockup.html, экран "Задачи": «Сегодня» / «На неделе»).
 */
@Entity(tableName = "tasks")
data class TaskItem(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val title: String,
    val assignedToId: String? = null,
    val isDone: Boolean = false,
    val dueBucket: String = "none", // "today" | "week" | "none"
    val recurrence: String = "none", // "none" | "daily"
    val createdAt: Long = System.currentTimeMillis(),
) {
    companion object {
        fun fromFirestoreMap(id: String, data: Map<String, Any?>): TaskItem = TaskItem(
            id = id,
            title = data["title"] as? String ?: "",
            assignedToId = data["assignedToId"] as? String,
            isDone = data["isDone"] as? Boolean ?: false,
            dueBucket = data["dueBucket"] as? String ?: "none",
            recurrence = data["recurrence"] as? String ?: "none",
            createdAt = (data["createdAt"] as? Number)?.toLong() ?: System.currentTimeMillis(),
        )
    }
}

fun TaskItem.toFirestoreMap(): Map<String, Any?> = mapOf(
    "title" to title,
    "assignedToId" to assignedToId,
    "isDone" to isDone,
    "dueBucket" to dueBucket,
    "recurrence" to recurrence,
    "createdAt" to createdAt,
)
