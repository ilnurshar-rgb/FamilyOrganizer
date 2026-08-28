package com.family.organizer.ui.finance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.family.organizer.data.Category
import com.family.organizer.data.CategoryRepository
import com.family.organizer.data.MoneyTransactionRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AddTransactionViewModel(
    private val transactionRepository: MoneyTransactionRepository,
    private val categoryRepository: CategoryRepository,
) : ViewModel() {

    val expenseCategories: StateFlow<List<Category>> = categoryRepository.observeByType("expense")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val incomeCategories: StateFlow<List<Category>> = categoryRepository.observeByType("income")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun addTransaction(amount: Double, type: String, categoryId: String?, onDone: () -> Unit) {
        viewModelScope.launch {
            transactionRepository.addTransaction(amount = amount, type = type, categoryId = categoryId)
            onDone()
        }
    }
}

class AddTransactionViewModelFactory(
    private val transactionRepository: MoneyTransactionRepository,
    private val categoryRepository: CategoryRepository,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        AddTransactionViewModel(transactionRepository, categoryRepository) as T
}
