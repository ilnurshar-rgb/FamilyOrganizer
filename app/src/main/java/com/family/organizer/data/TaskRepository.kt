package com.family.organizer.data

import com.family.organizer.data.family.FamilySession
import com.family.organizer.data.sync.SyncedRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow

class TaskRepository(
    private val dao: TaskDao,
    firestore: FirebaseFirestore,
    familySession: FamilySession,
) : SyncedRepository<TaskItem>(
    firestore = firestore,
    collectionName = "tasks",
    familySession = familySession,
    toMap = { it.toFirestoreMap() },
    fromMap = TaskItem::fromFirestoreMap,
    upsertLocal = { dao.upsert(it) },
    deleteLocal = { dao.deleteById(it) },
) {

    fun observeTasks(): Flow<List<TaskItem>> = dao.observeAll()

    suspend fun addTask(title: String, assignedToId: String?, dueBucket: String, recurrence: String = "none") {
        if (title.isBlank()) return
        val task = TaskItem(title = title.trim(), assignedToId = assignedToId, dueBucket = dueBucket, recurrence = recurrence)
        dao.insert(task)
        sync.push(task, task.id)
    }

    suspend fun setDone(task: TaskItem, isDone: Boolean) {
        val updated = task.copy(isDone = isDone)
        dao.update(updated)
        sync.push(updated, updated.id)
    }

    suspend fun delete(task: TaskItem) {
        dao.deleteById(task.id)
        sync.deleteRemote(task.id)
    }
}
