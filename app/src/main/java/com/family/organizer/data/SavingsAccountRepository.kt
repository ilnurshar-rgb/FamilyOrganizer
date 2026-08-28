package com.family.organizer.data

import com.family.organizer.data.family.FamilySession
import com.family.organizer.data.sync.SyncedRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow

class SavingsAccountRepository(
    private val dao: SavingsAccountDao,
    firestore: FirebaseFirestore,
    familySession: FamilySession,
) : SyncedRepository<SavingsAccount>(
    firestore = firestore,
    collectionName = "savings_accounts",
    familySession = familySession,
    toMap = { it.toFirestoreMap() },
    fromMap = SavingsAccount::fromFirestoreMap,
    upsertLocal = { dao.upsert(it) },
    deleteLocal = { dao.deleteById(it) },
) {

    fun observeAccounts(): Flow<List<SavingsAccount>> = dao.observeAll()

    suspend fun addAccount(title: String, icon: String, colorSlot: Int, targetAmount: Double?, linkedGoalId: String? = null) {
        if (title.isBlank()) return
        val account = SavingsAccount(
            title = title.trim(),
            icon = icon.ifBlank { "🏦" },
            colorSlot = colorSlot,
            targetAmount = targetAmount,
            linkedGoalId = linkedGoalId,
        )
        dao.insert(account)
        sync.push(account, account.id)
    }

    suspend fun contribute(account: SavingsAccount, amount: Double) {
        if (amount <= 0.0) return
        val updated = account.copy(currentAmount = account.currentAmount + amount)
        dao.update(updated)
        sync.push(updated, updated.id)
    }
}
