package com.family.organizer.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

/**
 * Пункт списка желаний. ownerId == null — общее семейное желание
 * (вкладка «Общие»), иначе — личное желание конкретного члена семьи.
 * Одна таблица вместо двух — см. family-app-architecture.md.
 */
@Entity(tableName = "wishlist_items")
data class WishlistItem(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val title: String,
    val ownerId: String? = null,
    val addedById: String? = null,
    val link: String? = null,
    val price: Double? = null,
    val isReserved: Boolean = false,
    val linkedGoalId: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
) {
    companion object {
        fun fromFirestoreMap(id: String, data: Map<String, Any?>): WishlistItem = WishlistItem(
            id = id,
            title = data["title"] as? String ?: "",
            ownerId = data["ownerId"] as? String,
            addedById = data["addedById"] as? String,
            link = data["link"] as? String,
            price = (data["price"] as? Number)?.toDouble(),
            isReserved = data["isReserved"] as? Boolean ?: false,
            linkedGoalId = data["linkedGoalId"] as? String,
            createdAt = (data["createdAt"] as? Number)?.toLong() ?: System.currentTimeMillis(),
        )
    }
}

fun WishlistItem.toFirestoreMap(): Map<String, Any?> = mapOf(
    "title" to title,
    "ownerId" to ownerId,
    "addedById" to addedById,
    "link" to link,
    "price" to price,
    "isReserved" to isReserved,
    "linkedGoalId" to linkedGoalId,
    "createdAt" to createdAt,
)
