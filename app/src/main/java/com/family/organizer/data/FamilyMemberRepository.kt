package com.family.organizer.data

import com.family.organizer.data.family.FamilySession
import com.family.organizer.data.sync.SyncedRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow

class FamilyMemberRepository(
    private val dao: FamilyMemberDao,
    firestore: FirebaseFirestore,
    familySession: FamilySession,
) : SyncedRepository<FamilyMember>(
    firestore = firestore,
    collectionName = "family_members",
    familySession = familySession,
    toMap = { it.toFirestoreMap() },
    fromMap = FamilyMember::fromFirestoreMap,
    upsertLocal = { dao.upsert(it) },
    deleteLocal = { dao.deleteById(it) },
) {

    fun observeMembers(): Flow<List<FamilyMember>> = dao.observeAll()

    suspend fun rename(member: FamilyMember, newName: String) {
        val trimmed = newName.trim()
        if (trimmed.isBlank() || trimmed == member.name) return
        val updated = member.copy(name = trimmed)
        dao.upsert(updated)
        sync.push(updated, updated.id)
    }
}
