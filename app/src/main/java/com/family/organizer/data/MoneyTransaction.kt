package com.family.organizer.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

/**
 * Операция — расход, доход или пополнение копилки. Названа MoneyTransaction,
 * а не Transaction, чтобы не конфликтовать с аннотацией androidx.room.Transaction.
 *
 * Учёт только по категориям, без детализации внутри категории — см.
 * family-app-architecture.md, раздел «Финансы».
 */
@Entity(tableName = "transactions")
data class MoneyTransaction(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val amount: Double,
    val type: String, // "expense" | "income" | "saving"
    val categoryId: String? = null,
    val savingsAccountId: String? = null,
    val authorId: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
) {
    companion object {
        fun fromFirestoreMap(id: String, data: Map<String, Any?>): MoneyTransaction = MoneyTransaction(
            id = id,
            amount = (data["amount"] as? Number)?.toDouble() ?: 0.0,
            type = data["type"] as? String ?: "expense",
            categoryId = data["categoryId"] as? String,
            savingsAccountId = data["savingsAccountId"] as? String,
            authorId = data["authorId"] as? String,
            createdAt = (data["createdAt"] as? Number)?.toLong() ?: System.currentTimeMillis(),
        )
    }
}

fun MoneyTransaction.toFirestoreMap(): Map<String, Any?> = mapOf(
    "amount" to amount,
    "type" to type,
    "categoryId" to categoryId,
    "savingsAccountId" to savingsAccountId,
    "authorId" to authorId,
    "createdAt" to createdAt,
)
