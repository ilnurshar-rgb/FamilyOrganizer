package com.family.organizer.data

import com.family.organizer.data.family.FamilySession
import com.family.organizer.data.sync.SyncedRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow

class WishlistItemRepository(
    private val dao: WishlistItemDao,
    firestore: FirebaseFirestore,
    familySession: FamilySession,
) : SyncedRepository<WishlistItem>(
    firestore = firestore,
    collectionName = "wishlist_items",
    familySession = familySession,
    toMap = { it.toFirestoreMap() },
    fromMap = WishlistItem::fromFirestoreMap,
    upsertLocal = { dao.upsert(it) },
    deleteLocal = { dao.deleteById(it) },
) {

    fun observeItems(): Flow<List<WishlistItem>> = dao.observeAll()

    suspend fun addItem(title: String, ownerId: String?, addedById: String?, price: Double?, link: String? = null) {
        if (title.isBlank()) return
        val item = WishlistItem(
            title = title.trim(),
            ownerId = ownerId,
            addedById = addedById,
            price = price,
            link = link?.ifBlank { null },
        )
        dao.insert(item)
        sync.push(item, item.id)
    }

    suspend fun setReserved(item: WishlistItem, isReserved: Boolean) {
        val updated = item.copy(isReserved = isReserved)
        dao.update(updated)
        sync.push(updated, updated.id)
    }

    suspend fun delete(item: WishlistItem) {
        dao.deleteById(item.id)
        sync.deleteRemote(item.id)
    }
}
