package com.family.organizer.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

/**
 * Копилка (накопление). Свободная (targetAmount == null) или связанная
 * с целью (linkedGoalId != null) — см. family-app-architecture.md.
 */
@Entity(tableName = "savings_accounts")
data class SavingsAccount(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val title: String,
    val icon: String = "🏦",
    val colorSlot: Int = 1,
    val currentAmount: Double = 0.0,
    val targetAmount: Double? = null,
    val linkedGoalId: String? = null,
) {
    companion object {
        fun fromFirestoreMap(id: String, data: Map<String, Any?>): SavingsAccount = SavingsAccount(
            id = id,
            title = data["title"] as? String ?: "",
            icon = data["icon"] as? String ?: "🏦",
            colorSlot = (data["colorSlot"] as? Number)?.toInt() ?: 1,
            currentAmount = (data["currentAmount"] as? Number)?.toDouble() ?: 0.0,
            targetAmount = (data["targetAmount"] as? Number)?.toDouble(),
            linkedGoalId = data["linkedGoalId"] as? String,
        )
    }
}

fun SavingsAccount.toFirestoreMap(): Map<String, Any?> = mapOf(
    "title" to title,
    "icon" to icon,
    "colorSlot" to colorSlot,
    "currentAmount" to currentAmount,
    "targetAmount" to targetAmount,
    "linkedGoalId" to linkedGoalId,
)
