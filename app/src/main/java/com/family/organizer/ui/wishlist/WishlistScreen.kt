package com.family.organizer.ui.wishlist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.family.organizer.data.WishlistItem
import com.family.organizer.ui.common.AddRow
import com.family.organizer.ui.common.BackButton
import com.family.organizer.ui.common.FilterChipLike
import com.family.organizer.ui.common.SimpleAddDialog
import com.family.organizer.ui.common.formatMoney

private const val SHARED_TAB = "shared"

@Composable
fun WishlistScreen(viewModel: WishlistViewModel, onBack: () -> Unit) {
    val items by viewModel.items.collectAsState()
    val members by viewModel.familyMembers.collectAsState()
    val memberById = members.associateBy { it.id }

    var selectedTab by remember { mutableStateOf(SHARED_TAB) }
    var showAddDialog by remember { mutableStateOf(false) }
    var newTitle by remember { mutableStateOf("") }
    var newPrice by remember { mutableStateOf("") }
    var newAddedById by remember { mutableStateOf<String?>(null) }

    val selectedOwnerId = if (selectedTab == SHARED_TAB) null else selectedTab
    val visibleItems = items.filter { it.ownerId == selectedOwnerId }

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BackButton(onClick = onBack)
            Text("Желания", style = MaterialTheme.typography.headlineSmall)
        }

        Box(modifier = Modifier.padding(bottom = 12.dp)) {
            AddRow(label = "Добавить желание…", onClick = { showAddDialog = true })
        }

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(bottom = 12.dp)) {
            item { FilterChipLike(text = "Общие", selected = selectedTab == SHARED_TAB) { selectedTab = SHARED_TAB } }
            items(members, key = { it.id }) { member ->
                FilterChipLike(text = member.name, selected = selectedTab == member.id) { selectedTab = member.id }
            }
        }

        LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            if (visibleItems.isEmpty()) {
                item {
                    Text(
                        "Пока пусто — самое время что-нибудь добавить",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 16.dp),
                    )
                }
            }
            items(visibleItems, key = { it.id }) { wishlistItem ->
                WishRow(
                    item = wishlistItem,
                    addedByName = memberById[wishlistItem.addedById]?.name,
                    onToggleReserved = { viewModel.setReserved(wishlistItem, !wishlistItem.isReserved) },
                    onDelete = { viewModel.delete(wishlistItem) },
                )
            }
            item { Box(modifier = Modifier.height(8.dp)) }
        }
    }

    if (showAddDialog) {
        SimpleAddDialog(
            title = "Новое желание",
            confirmEnabled = newTitle.isNotBlank(),
            onDismiss = { showAddDialog = false; newTitle = ""; newPrice = ""; newAddedById = null },
            onConfirm = {
                viewModel.addItem(newTitle, selectedOwnerId, newAddedById, newPrice.replace(",", ".").toDoubleOrNull())
                showAddDialog = false; newTitle = ""; newPrice = ""; newAddedById = null
            },
        ) {
            OutlinedTextField(value = newTitle, onValueChange = { newTitle = it }, label = { Text("Что хочется") })
            OutlinedTextField(
                value = newPrice,
                onValueChange = { value -> if (value.all { it.isDigit() || it == '.' || it == ',' }) newPrice = value },
                label = { Text("Цена, ₽ (необязательно)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            )
            Text("Кто добавил (необязательно)", style = MaterialTheme.typography.labelMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                members.forEach { member ->
                    FilterChipLike(text = member.name, selected = newAddedById == member.id) {
                        newAddedById = if (newAddedById == member.id) null else member.id
                    }
                }
            }
        }
    }
}

@Composable
private fun WishRow(item: WishlistItem, addedByName: String?, onToggleReserved: () -> Unit, onDelete: () -> Unit) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(item.title, style = MaterialTheme.typography.bodyLarge)
                val subtitle = listOfNotNull(
                    item.price?.let { formatMoney(it) },
                    addedByName?.let { "добавил(а) $it" },
                ).joinToString(" · ")
                if (subtitle.isNotBlank()) {
                    Text(subtitle, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            FilterChipLike(text = if (item.isReserved) "Уже дарят" else "Отметить «дарят»", selected = item.isReserved, onClick = onToggleReserved)

            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Close, contentDescription = "Удалить", tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
