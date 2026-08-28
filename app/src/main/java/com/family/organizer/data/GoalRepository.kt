package com.family.organizer.data

import com.family.organizer.data.family.FamilySession
import com.family.organizer.data.sync.SyncedRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow

class GoalRepository(
    private val dao: GoalDao,
    firestore: FirebaseFirestore,
    familySession: FamilySession,
) : SyncedRepository<Goal>(
    firestore = firestore,
    collectionName = "goals",
    familySession = familySession,
    toMap = { it.toFirestoreMap() },
    fromMap = Goal::fromFirestoreMap,
    upsertLocal = { dao.upsert(it) },
    deleteLocal = { dao.deleteById(it) },
) {

    fun observeGoals(): Flow<List<Goal>> = dao.observeAll()

    suspend fun addGoal(
        title: String,
        icon: String,
        colorSlot: Int,
        targetAmount: Double?,
        deadlineLabel: String?,
        contributorsLabel: String?,
    ) {
        if (title.isBlank()) return
        val goal = Goal(
            title = title.trim(),
            icon = icon.ifBlank { "🎯" },
            colorSlot = colorSlot,
            targetAmount = targetAmount,
            deadlineLabel = deadlineLabel?.ifBlank { null },
            contributorsLabel = contributorsLabel?.ifBlank { null },
        )
        dao.insert(goal)
        sync.push(goal, goal.id)
    }

    suspend fun contribute(goal: Goal, amount: Double) {
        if (amount <= 0.0) return
        val updated = goal.copy(currentAmount = goal.currentAmount + amount)
        dao.update(updated)
        sync.push(updated, updated.id)
    }
}
