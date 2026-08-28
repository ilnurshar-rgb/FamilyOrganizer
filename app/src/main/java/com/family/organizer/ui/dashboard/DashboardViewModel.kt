package com.family.organizer.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.family.organizer.data.CalendarEvent
import com.family.organizer.data.CalendarEventRepository
import com.family.organizer.data.Goal
import com.family.organizer.data.GoalRepository
import com.family.organizer.data.MoneyTransaction
import com.family.organizer.data.MoneyTransactionRepository
import com.family.organizer.data.ShoppingItem
import com.family.organizer.data.ShoppingRepository
import com.family.organizer.data.TaskItem
import com.family.organizer.data.TaskRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class DashboardViewModel(
    transactionRepository: MoneyTransactionRepository,
    goalRepository: GoalRepository,
    taskRepository: TaskRepository,
    shoppingRepository: ShoppingRepository,
    calendarEventRepository: CalendarEventRepository,
) : ViewModel() {

    val transactions: StateFlow<List<MoneyTransaction>> = transactionRepository.observeTransactions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val goals: StateFlow<List<Goal>> = goalRepository.observeGoals()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val tasks: StateFlow<List<TaskItem>> = taskRepository.observeTasks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val shoppingItems: StateFlow<List<ShoppingItem>> = shoppingRepository.observeItems()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val events: StateFlow<List<CalendarEvent>> = calendarEventRepository.observeEvents()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
}

class DashboardViewModelFactory(
    private val transactionRepository: MoneyTransactionRepository,
    private val goalRepository: GoalRepository,
    private val taskRepository: TaskRepository,
    private val shoppingRepository: ShoppingRepository,
    private val calendarEventRepository: CalendarEventRepository,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        DashboardViewModel(transactionRepository, goalRepository, taskRepository, shoppingRepository, calendarEventRepository) as T
}
