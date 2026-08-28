package com.family.organizer.data.family

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Текущая активная семья пользователя (или null — не в семье/не авторизован).
 * AuthViewModel обновляет это значение по мере входа/выхода и выбора семьи.
 * Все репозитории с облачной синхронизацией (см. data/sync) подписаны на
 * него и включают/выключают Firestore-слушатели при смене значения —
 * единая точка правды вместо того, чтобы прокидывать familyId в каждый
 * ViewModel и экран.
 */
class FamilySession {
    private val _familyId = MutableStateFlow<String?>(null)
    val familyId: StateFlow<String?> = _familyId.asStateFlow()

    fun setFamilyId(familyId: String?) {
        _familyId.value = familyId
    }
}
