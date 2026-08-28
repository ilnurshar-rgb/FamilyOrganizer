package com.family.organizer.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

/**
 * Член семьи. id — UUID (а не автоинкремент), чтобы совпадать на всех
 * устройствах семьи при синхронизации через Firestore (см. data/sync).
 */
@Entity(tableName = "family_members")
data class FamilyMember(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    val role: String = "adult", // "adult" | "child"
    val subtitle: String = "",  // например "Мама", "10 лет"
    val colorSlot: Int = 1,
) {
    companion object {
        fun fromFirestoreMap(id: String, data: Map<String, Any?>): FamilyMember = FamilyMember(
            id = id,
            name = data["name"] as? String ?: "",
            role = data["role"] as? String ?: "adult",
            subtitle = data["subtitle"] as? String ?: "",
            colorSlot = (data["colorSlot"] as? Number)?.toInt() ?: 1,
        )
    }
}

fun FamilyMember.toFirestoreMap(): Map<String, Any?> = mapOf(
    "name" to name,
    "role" to role,
    "subtitle" to subtitle,
    "colorSlot" to colorSlot,
)
