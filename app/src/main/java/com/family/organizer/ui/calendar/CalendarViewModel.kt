package com.family.organizer.ui.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.family.organizer.data.CalendarEvent
import com.family.organizer.data.CalendarEventRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * События календаря. Наложение дедлайнов задач/целей (см.
 * family-app-architecture.md, «Календарь как агрегатор») — следующий шаг:
 * в первой версии задачи и цели хранят срок как метку ("сегодня"/"на
 * неделе"/текст), а не точную дату, поэтому пока их нельзя разместить
 * на числовой сетке месяца.
 */
class CalendarViewModel(private val repository: CalendarEventRepository) : ViewModel() {

    val events: StateFlow<List<CalendarEvent>> = repository.observeEvents()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun addEvent(
        title: String,
        type: String,
        dateEpochDay: Long,
        timeLabel: String?,
        allDay: Boolean,
        recurrence: String,
    ) {
        viewModelScope.launch {
            repository.addEvent(title, type, dateEpochDay, timeLabel, allDay, recurrence)
        }
    }
}

class CalendarViewModelFactory(private val repository: CalendarEventRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        CalendarViewModel(repository) as T
}
