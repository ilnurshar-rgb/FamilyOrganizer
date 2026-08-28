package com.family.organizer.data.family

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.random.Random
import java.util.UUID

private const val USERS_COLLECTION = "users"
private const val FAMILIES_COLLECTION = "families"
private const val INVITE_CODE_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"

/**
 * Firestore-структура (см. также правила безопасности в консоли Firebase):
 *  - users/{uid}: { email, displayName, familyId }
 *  - families/{inviteCode}: { name, inviteCode, ownerUid, memberUids: [uid...], createdAt }
 *  - families/{inviteCode}/family_members, /categories, /savings_accounts,
 *    /transactions, /goals, /tasks, /calendar_events, /wishlist_items,
 *    /shopping_items — данные семьи, синхронизируются с Room на каждом
 *    устройстве через data/sync/CloudCollectionSync.
 *
 * Код приглашения используется как ID документа семьи — это позволяет
 * находить семью по коду простым get() без Firestore-запросов и
 * составных индексов (сознательное упрощение, снижающее риск).
 *
 * Стартовые данные (члены семьи по умолчанию, категории, копилка) сеются
 * один раз сюда при создании семьи (см. seedFamilyDefaults) — не локально
 * в Room на каждом устройстве, иначе у каждого члена семьи появился бы свой
 * независимый набор «Анна/Игорь/Соня/Макс».
 */
class FamilyCloudRepository(private val firestore: FirebaseFirestore) {

    suspend fun ensureUserDoc(uid: String, email: String, displayName: String): Result<Unit> =
        setDocument(USERS_COLLECTION, uid, mapOf("email" to email, "displayName" to displayName), merge = true)

    fun observeUserFamilyId(uid: String): Flow<String?> = callbackFlow {
        val registration = firestore.collection(USERS_COLLECTION).document(uid)
            .addSnapshotListener { snapshot, _ -> trySend(snapshot?.getString("familyId")) }
        awaitClose { registration.remove() }
    }

    suspend fun createFamily(name: String, ownerUid: String): Result<String> {
        repeat(5) {
            val code = generateInviteCode()
            val ref = firestore.collection(FAMILIES_COLLECTION).document(code)

            val existingResult = getDocument(ref)
            if (existingResult.isFailure) return Result.failure(existingResult.exceptionOrNull()!!)
            val existing = existingResult.getOrNull()

            if (existing == null || !existing.exists()) {
                val data = mapOf(
                    "name" to name,
                    "inviteCode" to code,
                    "ownerUid" to ownerUid,
                    "memberUids" to listOf(ownerUid),
                    "createdAt" to System.currentTimeMillis(),
                )
                val createResult = setDocumentRef(ref, data, merge = false)
                if (createResult.isFailure) return Result.failure(createResult.exceptionOrNull()!!)

                val userResult = setDocument(USERS_COLLECTION, ownerUid, mapOf("familyId" to code), merge = true)
                if (userResult.isFailure) return Result.failure(userResult.exceptionOrNull()!!)

                seedFamilyDefaults(code)

                return Result.success(code)
            }
        }
        return Result.failure(IllegalStateException("Не удалось создать код приглашения, попробуйте ещё раз"))
    }

    suspend fun joinFamily(inviteCode: String, uid: String): Result<Unit> {
        if (inviteCode.isBlank()) return Result.failure(IllegalArgumentException("Введите код приглашения"))

        val ref = firestore.collection(FAMILIES_COLLECTION).document(inviteCode)
        val snapshotResult = getDocument(ref)
        if (snapshotResult.isFailure) return Result.failure(snapshotResult.exceptionOrNull()!!)
        val snapshot = snapshotResult.getOrNull()

        if (snapshot == null || !snapshot.exists()) {
            return Result.failure(IllegalArgumentException("Код приглашения не найден"))
        }

        val members = (snapshot.get("memberUids") as? List<*>)?.filterIsInstance<String>() ?: emptyList()
        if (uid !in members) {
            val updateResult = setDocumentRef(ref, mapOf("memberUids" to members + uid), merge = true)
            if (updateResult.isFailure) return Result.failure(updateResult.exceptionOrNull()!!)
        }

        val userResult = setDocument(USERS_COLLECTION, uid, mapOf("familyId" to inviteCode), merge = true)
        if (userResult.isFailure) return Result.failure(userResult.exceptionOrNull()!!)

        return Result.success(Unit)
    }

    /**
     * Стартовые данные семьи — имена и палитра совпадают с HTML-макетом
     * (family-app-mockup.html), пишутся один раз при создании семьи.
     * Запись без ожидания ответа — Firestore сам досылает при потере сети,
     * а на устройство создателя данные придут обычным путём: через
     * CloudCollectionSync, как только AuthViewModel включит synced-репозитории
     * для новой семьи.
     */
    private fun seedFamilyDefaults(familyId: String) {
        val familyRef = firestore.collection(FAMILIES_COLLECTION).document(familyId)

        val members = listOf(
            mapOf("name" to "Анна", "role" to "adult", "subtitle" to "Мама", "colorSlot" to 5),
            mapOf("name" to "Игорь", "role" to "adult", "subtitle" to "Папа", "colorSlot" to 1),
            mapOf("name" to "Соня", "role" to "child", "subtitle" to "10 лет", "colorSlot" to 4),
            mapOf("name" to "Макс", "role" to "child", "subtitle" to "15 лет", "colorSlot" to 3),
        )
        members.forEach { data ->
            familyRef.collection("family_members").document(UUID.randomUUID().toString()).set(data)
        }

        val categories = listOf(
            mapOf("name" to "Продукты", "type" to "expense", "icon" to "🛒", "colorSlot" to 1, "isDefault" to true, "sortOrder" to 0),
            mapOf("name" to "Транспорт", "type" to "expense", "icon" to "🚗", "colorSlot" to 2, "isDefault" to true, "sortOrder" to 1),
            mapOf("name" to "Развлечения", "type" to "expense", "icon" to "🎬", "colorSlot" to 5, "isDefault" to true, "sortOrder" to 2),
            mapOf("name" to "Коммуналка", "type" to "expense", "icon" to "🏠", "colorSlot" to 4, "isDefault" to true, "sortOrder" to 3),
            mapOf("name" to "Здоровье", "type" to "expense", "icon" to "💊", "colorSlot" to 3, "isDefault" to true, "sortOrder" to 4),
            mapOf("name" to "Прочее", "type" to "expense", "icon" to "📦", "colorSlot" to 7, "isDefault" to true, "sortOrder" to 5),
            mapOf("name" to "Зарплата", "type" to "income", "icon" to "💼", "colorSlot" to 3, "isDefault" to true, "sortOrder" to 0),
            mapOf("name" to "Подработка", "type" to "income", "icon" to "💻", "colorSlot" to 1, "isDefault" to true, "sortOrder" to 1),
            mapOf("name" to "Подарки", "type" to "income", "icon" to "🎁", "colorSlot" to 5, "isDefault" to true, "sortOrder" to 2),
            mapOf("name" to "Проценты", "type" to "income", "icon" to "🏦", "colorSlot" to 7, "isDefault" to true, "sortOrder" to 3),
            mapOf("name" to "Прочее", "type" to "income", "icon" to "📦", "colorSlot" to 4, "isDefault" to true, "sortOrder" to 4),
        )
        categories.forEach { data ->
            familyRef.collection("categories").document(UUID.randomUUID().toString()).set(data)
        }

        val savingsAccount = mapOf(
            "title" to "Подушка безопасности",
            "icon" to "🛡️",
            "colorSlot" to 3,
            "currentAmount" to 0.0,
            "targetAmount" to null,
            "linkedGoalId" to null,
        )
        familyRef.collection("savings_accounts").document(UUID.randomUUID().toString()).set(savingsAccount)
    }

    private fun generateInviteCode(): String =
        (1..6).map { INVITE_CODE_CHARS[Random.nextInt(INVITE_CODE_CHARS.length)] }.joinToString("")

    private suspend fun setDocument(collection: String, docId: String, data: Map<String, Any?>, merge: Boolean): Result<Unit> =
        setDocumentRef(firestore.collection(collection).document(docId), data, merge)

    private suspend fun setDocumentRef(ref: DocumentReference, data: Map<String, Any?>, merge: Boolean): Result<Unit> =
        suspendCancellableCoroutine { cont ->
            val task = if (merge) ref.set(data, SetOptions.merge()) else ref.set(data)
            task.addOnSuccessListener { cont.resume(Result.success(Unit)) }
                .addOnFailureListener { e -> cont.resume(Result.failure(e)) }
        }

    private suspend fun getDocument(ref: DocumentReference): Result<DocumentSnapshot> =
        suspendCancellableCoroutine { cont ->
            ref.get()
                .addOnSuccessListener { snapshot -> cont.resume(Result.success(snapshot)) }
                .addOnFailureListener { e -> cont.resume(Result.failure(e)) }
        }
}
