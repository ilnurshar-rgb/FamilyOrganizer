package com.family.organizer.ui.goals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.family.organizer.data.Goal
import com.family.organizer.data.GoalRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class GoalsViewModel(private val repository: GoalRepository) : ViewModel() {

    val goals: StateFlow<List<Goal>> = repository.observeGoals()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun addGoal(title: String, icon: String, colorSlot: Int, targetAmount: Double?, deadlineLabel: String?, contributorsLabel: String?) {
        viewModelScope.launch { repository.addGoal(title, icon, colorSlot, targetAmount, deadlineLabel, contributorsLabel) }
    }

    fun contribute(goal: Goal, amount: Double) {
        viewModelScope.launch { repository.contribute(goal, amount) }
    }
}

class GoalsViewModelFactory(private val repository: GoalRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        GoalsViewModel(repository) as T
}
