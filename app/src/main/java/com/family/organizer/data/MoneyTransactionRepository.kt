package com.family.organizer.data

import com.family.organizer.data.family.FamilySession
import com.family.organizer.data.sync.SyncedRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow

class MoneyTransactionRepository(
    private val dao: MoneyTransactionDao,
    firestore: FirebaseFirestore,
    familySession: FamilySession,
) : SyncedRepository<MoneyTransaction>(
    firestore = firestore,
    collectionName = "transactions",
    familySession = familySession,
    toMap = { it.toFirestoreMap() },
    fromMap = MoneyTransaction::fromFirestoreMap,
    upsertLocal = { dao.upsert(it) },
    deleteLocal = { dao.deleteById(it) },
) {

    fun observeTransactions(): Flow<List<MoneyTransaction>> = dao.observeAll()

    suspend fun addTransaction(
        amount: Double,
        type: String,
        categoryId: String? = null,
        savingsAccountId: String? = null,
        authorId: String? = null,
    ) {
        if (amount <= 0.0) return
        val transaction = MoneyTransaction(
            amount = amount,
            type = type,
            categoryId = categoryId,
            savingsAccountId = savingsAccountId,
            authorId = authorId,
        )
        dao.insert(transaction)
        sync.push(transaction, transaction.id)
    }
}
