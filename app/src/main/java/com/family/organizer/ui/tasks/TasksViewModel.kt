package com.family.organizer.ui.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.family.organizer.data.FamilyMember
import com.family.organizer.data.FamilyMemberRepository
import com.family.organizer.data.TaskItem
import com.family.organizer.data.TaskRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TasksViewModel(
    private val taskRepository: TaskRepository,
    private val familyMemberRepository: FamilyMemberRepository,
) : ViewModel() {

    val tasks: StateFlow<List<TaskItem>> = taskRepository.observeTasks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val familyMembers: StateFlow<List<FamilyMember>> = familyMemberRepository.observeMembers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun addTask(title: String, assignedToId: String?, dueBucket: String, recurrence: String) {
        viewModelScope.launch { taskRepository.addTask(title, assignedToId, dueBucket, recurrence) }
    }

    fun setDone(task: TaskItem, isDone: Boolean) {
        viewModelScope.launch { taskRepository.setDone(task, isDone) }
    }

    fun delete(task: TaskItem) {
        viewModelScope.launch { taskRepository.delete(task) }
    }
}

class TasksViewModelFactory(
    private val taskRepository: TaskRepository,
    private val familyMemberRepository: FamilyMemberRepository,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        TasksViewModel(taskRepository, familyMemberRepository) as T
}
