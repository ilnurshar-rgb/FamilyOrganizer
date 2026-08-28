package com.family.organizer.ui.finance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.family.organizer.data.Category
import com.family.organizer.data.CategoryRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CategoriesViewModel(private val repository: CategoryRepository) : ViewModel() {

    val expenseCategories: StateFlow<List<Category>> = repository.observeByType("expense")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val incomeCategories: StateFlow<List<Category>> = repository.observeByType("income")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun addCategory(name: String, type: String, icon: String, colorSlot: Int) {
        viewModelScope.launch { repository.addCategory(name, type, icon, colorSlot) }
    }

    fun delete(category: Category) {
        viewModelScope.launch { repository.delete(category) }
    }
}

class CategoriesViewModelFactory(private val repository: CategoryRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        CategoriesViewModel(repository) as T
}
