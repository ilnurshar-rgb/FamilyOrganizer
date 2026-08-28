package com.family.organizer.data

import com.family.organizer.data.family.FamilySession
import com.family.organizer.data.sync.SyncedRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow

class CategoryRepository(
    private val dao: CategoryDao,
    firestore: FirebaseFirestore,
    familySession: FamilySession,
) : SyncedRepository<Category>(
    firestore = firestore,
    collectionName = "categories",
    familySession = familySession,
    toMap = { it.toFirestoreMap() },
    fromMap = Category::fromFirestoreMap,
    upsertLocal = { dao.upsert(it) },
    deleteLocal = { dao.deleteById(it) },
) {

    fun observeByType(type: String): Flow<List<Category>> = dao.observeByType(type)

    fun observeAll(): Flow<List<Category>> = dao.observeAll()

    suspend fun addCategory(name: String, type: String, icon: String, colorSlot: Int) {
        if (name.isBlank()) return
        val category = Category(name = name.trim(), type = type, icon = icon.ifBlank { "📦" }, colorSlot = colorSlot)
        dao.insert(category)
        sync.push(category, category.id)
    }

    suspend fun delete(category: Category) {
        dao.delete(category)
        sync.deleteRemote(category.id)
    }
}
