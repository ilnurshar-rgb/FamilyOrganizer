package com.family.organizer.ui.finance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.family.organizer.data.Category
import com.family.organizer.data.CategoryRepository
import com.family.organizer.data.FamilyMember
import com.family.organizer.data.FamilyMemberRepository
import com.family.organizer.data.Goal
import com.family.organizer.data.GoalRepository
import com.family.organizer.data.MoneyTransaction
import com.family.organizer.data.MoneyTransactionRepository
import com.family.organizer.data.SavingsAccount
import com.family.organizer.data.SavingsAccountRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FinanceViewModel(
    private val transactionRepository: MoneyTransactionRepository,
    private val categoryRepository: CategoryRepository,
    private val savingsAccountRepository: SavingsAccountRepository,
    private val familyMemberRepository: FamilyMemberRepository,
    private val goalRepository: GoalRepository,
) : ViewModel() {

    val transactions: StateFlow<List<MoneyTransaction>> = transactionRepository.observeTransactions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val categories: StateFlow<List<Category>> = categoryRepository.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val savingsAccounts: StateFlow<List<SavingsAccount>> = savingsAccountRepository.observeAccounts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val familyMembers: StateFlow<List<FamilyMember>> = familyMemberRepository.observeMembers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val goals: StateFlow<List<Goal>> = goalRepository.observeGoals()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun addSavingsAccount(title: String, icon: String, colorSlot: Int, targetAmount: Double?) {
        viewModelScope.launch { savingsAccountRepository.addAccount(title, icon, colorSlot, targetAmount) }
    }

    fun contributeToSavings(account: SavingsAccount, amount: Double) {
        viewModelScope.launch {
            savingsAccountRepository.contribute(account, amount)
            transactionRepository.addTransaction(amount = amount, type = "saving", savingsAccountId = account.id)
            val linkedGoalId = account.linkedGoalId
            if (linkedGoalId != null) {
                val goal = goals.value.find { it.id == linkedGoalId }
                if (goal != null) goalRepository.contribute(goal, amount)
            }
        }
    }
}

class FinanceViewModelFactory(
    private val transactionRepository: MoneyTransactionRepository,
    private val categoryRepository: CategoryRepository,
    private val savingsAccountRepository: SavingsAccountRepository,
    private val familyMemberRepository: FamilyMemberRepository,
    private val goalRepository: GoalRepository,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        FinanceViewModel(transactionRepository, categoryRepository, savingsAccountRepository, familyMemberRepository, goalRepository) as T
}
