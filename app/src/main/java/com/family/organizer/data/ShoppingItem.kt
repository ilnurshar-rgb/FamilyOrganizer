package com.family.organizer.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

/**
 * Пункт списка покупок. Синхронизируется между устройствами семьи через
 * Firestore (см. data/sync/CloudCollectionSync) — Room остаётся локальным
 * источником правды для UI (offline-first, family-app-architecture.md).
 */
@Entity(tableName = "shopping_items")
data class ShoppingItem(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val title: String,
    val category: String = "Прочее",
    val isBought: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
) {
    companion object {
        fun fromFirestoreMap(id: String, data: Map<String, Any?>): ShoppingItem = ShoppingItem(
            id = id,
            title = data["title"] as? String ?: "",
            category = data["category"] as? String ?: "Прочее",
            isBought = data["isBought"] as? Boolean ?: false,
            createdAt = (data["createdAt"] as? Number)?.toLong() ?: System.currentTimeMillis(),
        )
    }
}

fun ShoppingItem.toFirestoreMap(): Map<String, Any?> = mapOf(
    "title" to title,
    "category" to category,
    "isBought" to isBought,
    "createdAt" to createdAt,
)
