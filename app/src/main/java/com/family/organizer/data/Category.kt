package com.family.organizer.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

/**
 * Категория дохода или расхода. Категории — данные семьи, а не хардкод:
 * список редактируется в Настройках, отдельно для расходов и доходов
 * (см. family-app-architecture.md, раздел «Финансы»).
 */
@Entity(tableName = "categories")
data class Category(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    val type: String, // "expense" | "income"
    val icon: String = "📦",
    val colorSlot: Int = 1,
    val isDefault: Boolean = false,
    val sortOrder: Int = 0,
) {
    companion object {
        fun fromFirestoreMap(id: String, data: Map<String, Any?>): Category = Category(
            id = id,
            name = data["name"] as? String ?: "",
            type = data["type"] as? String ?: "expense",
            icon = data["icon"] as? String ?: "📦",
            colorSlot = (data["colorSlot"] as? Number)?.toInt() ?: 1,
            isDefault = data["isDefault"] as? Boolean ?: false,
            sortOrder = (data["sortOrder"] as? Number)?.toInt() ?: 0,
        )
    }
}

fun Category.toFirestoreMap(): Map<String, Any?> = mapOf(
    "name" to name,
    "type" to type,
    "icon" to icon,
    "colorSlot" to colorSlot,
    "isDefault" to isDefault,
    "sortOrder" to sortOrder,
)
