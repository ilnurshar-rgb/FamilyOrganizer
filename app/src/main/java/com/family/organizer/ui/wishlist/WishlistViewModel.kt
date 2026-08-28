package com.family.organizer.ui.wishlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.family.organizer.data.FamilyMember
import com.family.organizer.data.FamilyMemberRepository
import com.family.organizer.data.WishlistItem
import com.family.organizer.data.WishlistItemRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class WishlistViewModel(
    private val wishlistRepository: WishlistItemRepository,
    private val familyMemberRepository: FamilyMemberRepository,
) : ViewModel() {

    val items: StateFlow<List<WishlistItem>> = wishlistRepository.observeItems()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val familyMembers: StateFlow<List<FamilyMember>> = familyMemberRepository.observeMembers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun addItem(title: String, ownerId: String?, addedById: String?, price: Double?) {
        viewModelScope.launch { wishlistRepository.addItem(title, ownerId, addedById, price) }
    }

    fun setReserved(item: WishlistItem, isReserved: Boolean) {
        viewModelScope.launch { wishlistRepository.setReserved(item, isReserved) }
    }

    fun delete(item: WishlistItem) {
        viewModelScope.launch { wishlistRepository.delete(item) }
    }
}

class WishlistViewModelFactory(
    private val wishlistRepository: WishlistItemRepository,
    private val familyMemberRepository: FamilyMemberRepository,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        WishlistViewModel(wishlistRepository, familyMemberRepository) as T
}
