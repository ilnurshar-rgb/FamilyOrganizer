package com.family.organizer.ui.more

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.family.organizer.data.CalendarEvent
import com.family.organizer.data.CalendarEventRepository
import com.family.organizer.data.FamilyMember
import com.family.organizer.data.FamilyMemberRepository
import com.family.organizer.data.Goal
import com.family.organizer.data.GoalRepository
import com.family.organizer.data.WishlistItem
import com.family.organizer.data.WishlistItemRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class MoreViewModel(
    familyMemberRepository: FamilyMemberRepository,
    calendarEventRepository: CalendarEventRepository,
    goalRepository: GoalRepository,
    wishlistItemRepository: WishlistItemRepository,
) : ViewModel() {

    val familyMembers: StateFlow<List<FamilyMember>> = familyMemberRepository.observeMembers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val events: StateFlow<List<CalendarEvent>> = calendarEventRepository.observeEvents()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val goals: StateFlow<List<Goal>> = goalRepository.observeGoals()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val wishlistItems: StateFlow<List<WishlistItem>> = wishlistItemRepository.observeItems()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
}

class MoreViewModelFactory(
    private val familyMemberRepository: FamilyMemberRepository,
    private val calendarEventRepository: CalendarEventRepository,
    private val goalRepository: GoalRepository,
    private val wishlistItemRepository: WishlistItemRepository,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        MoreViewModel(familyMemberRepository, calendarEventRepository, goalRepository, wishlistItemRepository) as T
}
