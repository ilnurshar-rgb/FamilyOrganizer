package com.family.organizer.data.sync

import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

private const val FAMILIES_COLLECTION = "families"

/**
 * Синхронизация одной Firestore-подколлекции семьи (families/{id}/<collection>)
 * с локальной Room-таблицей.
 *
 * "Облако → устройство": слушатель Firestore применяет добавления/изменения
 * через upsertLocal и удаления через deleteLocal.
 *
 * "Устройство → облако": push()/deleteRemote() — простая запись без ожидания
 * ответа сервера. Firestore SDK сам ставит операцию в очередь при отсутствии
 * сети и досылает её при восстановлении соединения (офлайн-персистентность
 * включена по умолчанию) — соответствует offline-first принципу проекта
 * (см. family-app-architecture.md).
 *
 * Сознательно без POJO-маппинга Firestore (.toObject()/.set(entity)) —
 * это требует no-arg конструктора и было бы ненадёжно для Kotlin data class
 * без возможности проверить компиляцией; вместо этого entity <-> Map
 * переводится вручную через toMap/fromMap, переданные каждым репозиторием.
 */
class CloudCollectionSync<T : Any>(
    private val firestore: FirebaseFirestore,
    private val collectionName: String,
    private val toMap: (T) -> Map<String, Any?>,
    private val fromMap: (id: String, data: Map<String, Any?>) -> T,
    private val upsertLocal: suspend (T) -> Unit,
    private val deleteLocal: suspend (String) -> Unit,
) {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var registration: ListenerRegistration? = null
    private var familyId: String? = null

    fun start(newFamilyId: String) {
        if (familyId == newFamilyId && registration != null) return
        stop()
        familyId = newFamilyId
        registration = collectionRef(newFamilyId).addSnapshotListener { snapshot, _ ->
            if (snapshot == null) return@addSnapshotListener
            for (change in snapshot.documentChanges) {
                val docId = change.document.id
                if (change.type == DocumentChange.Type.REMOVED) {
                    scope.launch { deleteLocal(docId) }
                } else {
                    val entity = fromMap(docId, change.document.data)
                    scope.launch { upsertLocal(entity) }
                }
            }
        }
    }

    fun stop() {
        registration?.remove()
        registration = null
        familyId = null
    }

    fun push(entity: T, id: String) {
        val currentFamilyId = familyId ?: return
        collectionRef(currentFamilyId).document(id).set(toMap(entity))
    }

    fun deleteRemote(id: String) {
        val currentFamilyId = familyId ?: return
        collectionRef(currentFamilyId).document(id).delete()
    }

    private fun collectionRef(familyId: String) =
        firestore.collection(FAMILIES_COLLECTION).document(familyId).collection(collectionName)
}
