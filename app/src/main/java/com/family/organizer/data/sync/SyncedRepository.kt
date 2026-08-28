package com.family.organizer.data.sync

import com.family.organizer.data.family.FamilySession
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Общий базовый класс для репозиториев с облачной синхронизацией: сам
 * включает/выключает Firestore-слушатель ([CloudCollectionSync]) при смене
 * текущей семьи ([FamilySession]). Наследники используют защищённое поле
 * [sync] внутри своих insert/update/delete-методов (push/deleteRemote).
 */
abstract class SyncedRepository<T : Any>(
    firestore: FirebaseFirestore,
    collectionName: String,
    familySession: FamilySession,
    toMap: (T) -> Map<String, Any?>,
    fromMap: (id: String, data: Map<String, Any?>) -> T,
    upsertLocal: suspend (T) -> Unit,
    deleteLocal: suspend (String) -> Unit,
) {
    protected val sync: CloudCollectionSync<T> = CloudCollectionSync(
        firestore = firestore,
        collectionName = collectionName,
        toMap = toMap,
        fromMap = fromMap,
        upsertLocal = upsertLocal,
        deleteLocal = deleteLocal,
    )

    private val syncScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    init {
        syncScope.launch {
            familySession.familyId.collect { id ->
                if (id != null) sync.start(id) else sync.stop()
            }
        }
    }
}
