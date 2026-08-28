package com.family.organizer.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

/**
 * Финансовая цель — см. family-app-architecture.md, раздел «Цели».
 * deadlineLabel/contributorsLabel — свободный текст вместо выбора даты/участников
 * из справочника: сознательное упрощение первой версии.
 */
@Entity(tableName = "goals")
data class Goal(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val title: String,
    val icon: String = "🎯",
    val colorSlot: Int = 1,
    val targetAmount: Double? = null,
    val currentAmount: Double = 0.0,
    val deadlineLabel: String? = null,
    val contributorsLabel: String? = null,
) {
    companion object {
        fun fromFirestoreMap(id: String, data: Map<String, Any?>): Goal = Goal(
            id = id,
            title = data["title"] as? String ?: "",
            icon = data["icon"] as? String ?: "🎯",
            colorSlot = (data["colorSlot"] as? Number)?.toInt() ?: 1,
            targetAmount = (data["targetAmount"] as? Number)?.toDouble(),
            currentAmount = (data["currentAmount"] as? Number)?.toDouble() ?: 0.0,
            deadlineLabel = data["deadlineLabel"] as? String,
            contributorsLabel = data["contributorsLabel"] as? String,
        )
    }
}

fun Goal.toFirestoreMap(): Map<String, Any?> = mapOf(
    "title" to title,
    "icon" to icon,
    "colorSlot" to colorSlot,
    "targetAmount" to targetAmount,
    "currentAmount" to currentAmount,
    "deadlineLabel" to deadlineLabel,
    "contributorsLabel" to contributorsLabel,
)
