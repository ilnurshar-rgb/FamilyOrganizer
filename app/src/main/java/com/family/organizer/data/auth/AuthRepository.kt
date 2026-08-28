package com.family.organizer.data.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * Тонкая обёртка над FirebaseAuth. Колбэки Firebase (Task<T>) вручную
 * оборачиваются в suspendCancellableCoroutine — без дополнительной
 * зависимости kotlinx-coroutines-play-services, чтобы не увеличивать
 * поверхность риска (см. общую дисциплину проекта — минимум новых
 * движущихся частей за один шаг, раз сборка проверяется только в CI).
 */
class AuthRepository(private val auth: FirebaseAuth) {

    val currentUserId: String?
        get() = auth.currentUser?.uid

    val currentUserEmail: String?
        get() = auth.currentUser?.email

    fun authState(): Flow<FirebaseUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth -> trySend(firebaseAuth.currentUser) }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    suspend fun signUp(email: String, password: String): Result<Unit> = suspendCancellableCoroutine { cont ->
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { cont.resume(Result.success(Unit)) }
            .addOnFailureListener { e -> cont.resume(Result.failure(e)) }
    }

    suspend fun signIn(email: String, password: String): Result<Unit> = suspendCancellableCoroutine { cont ->
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { cont.resume(Result.success(Unit)) }
            .addOnFailureListener { e -> cont.resume(Result.failure(e)) }
    }

    fun signOut() {
        auth.signOut()
    }
}
