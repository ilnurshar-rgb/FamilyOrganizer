package com.family.organizer.data

import com.family.organizer.data.family.FamilySession
import com.family.organizer.data.sync.SyncedRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow

class CalendarEventRepository(
    private val dao: CalendarEventDao,
    firestore: FirebaseFirestore,
    familySession: FamilySession,
) : SyncedRepository<CalendarEvent>(
    firestore = firestore,
    collectionName = "calendar_events",
    familySession = familySession,
    toMap = { it.toFirestoreMap() },
    fromMap = CalendarEvent::fromFirestoreMap,
    upsertLocal = { dao.upsert(it) },
    deleteLocal = { dao.deleteById(it) },
) {

    fun observeEvents(): Flow<List<CalendarEvent>> = dao.observeAll()

    suspend fun addEvent(
        title: String,
        type: String,
        dateEpochDay: Long,
        timeLabel: String?,
        allDay: Boolean,
        recurrence: String = "none",
        participantsLabel: String? = null,
    ) {
        if (title.isBlank()) return
        val event = CalendarEvent(
            title = title.trim(),
            type = type,
            dateEpochDay = dateEpochDay,
            timeLabel = timeLabel?.ifBlank { null },
            allDay = allDay,
            recurrence = recurrence,
            participantsLabel = participantsLabel?.ifBlank { null },
        )
        dao.insert(event)
        sync.push(event, event.id)
    }
}
