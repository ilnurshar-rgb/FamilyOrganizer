package com.family.organizer.ui.shopping

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.family.organizer.data.ShoppingItem
import com.family.organizer.data.ShoppingRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ShoppingViewModel(private val repository: ShoppingRepository) : ViewModel() {

    val items: StateFlow<List<ShoppingItem>> = repository.observeItems()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun addItem(title: String) {
        viewModelScope.launch { repository.addItem(title) }
    }

    fun toggleBought(item: ShoppingItem) {
        viewModelScope.launch { repository.setBought(item, !item.isBought) }
    }

    fun delete(item: ShoppingItem) {
        viewModelScope.launch { repository.delete(item) }
    }
}

class ShoppingViewModelFactory(private val repository: ShoppingRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        ShoppingViewModel(repository) as T
}
