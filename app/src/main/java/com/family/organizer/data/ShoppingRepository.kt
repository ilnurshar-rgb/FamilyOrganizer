package com.family.organizer.data

import com.family.organizer.data.family.FamilySession
import com.family.organizer.data.sync.SyncedRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow

/**
 * Репозиторий поверх Room с двусторонней синхронизацией через Firestore
 * (см. SyncedRepository/CloudCollectionSync) — Room остаётся источником
 * правды для UI, Firestore — каналом обмена между устройствами семьи.
 */
class ShoppingRepository(
    private val dao: ShoppingItemDao,
    firestore: FirebaseFirestore,
    familySession: FamilySession,
) : SyncedRepository<ShoppingItem>(
    firestore = firestore,
    collectionName = "shopping_items",
    familySession = familySession,
    toMap = { it.toFirestoreMap() },
    fromMap = ShoppingItem::fromFirestoreMap,
    upsertLocal = { dao.upsert(it) },
    deleteLocal = { dao.deleteById(it) },
) {

    fun observeItems(): Flow<List<ShoppingItem>> = dao.observeAll()

    suspend fun addItem(title: String, category: String = "Прочее") {
        if (title.isBlank()) return
        val item = ShoppingItem(title = title.trim(), category = category)
        dao.insert(item)
        sync.push(item, item.id)
    }

    suspend fun setBought(item: ShoppingItem, isBought: Boolean) {
        val updated = item.copy(isBought = isBought)
        dao.update(updated)
        sync.push(updated, updated.id)
    }

    suspend fun delete(item: ShoppingItem) {
        dao.deleteById(item.id)
        sync.deleteRemote(item.id)
    }
}
