package com.family.organizer.ui.finance

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import com.family.organizer.data.Category
import com.family.organizer.ui.common.AddRow
import com.family.organizer.ui.common.BackButton
import com.family.organizer.ui.common.FilterChipLike
import com.family.organizer.ui.common.SimpleAddDialog
import com.family.organizer.ui.common.iconBackgroundForSlot

@Composable
fun CategoriesScreen(viewModel: CategoriesViewModel, onBack: () -> Unit) {
    val expenseCategories by viewModel.expenseCategories.collectAsState()
    val incomeCategories by viewModel.incomeCategories.collectAsState()

    var selectedType by remember { mutableStateOf("expense") }
    var showAddDialog by remember { mutableStateOf(false) }
    var newName by remember { mutableStateOf("") }
    var newIcon by remember { mutableStateOf("📦") }

    val list = if (selectedType == "expense") expenseCategories else incomeCategories
    val nextColorSlot = (list.size % 8) + 1

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BackButton(onClick = onBack)
            Text("Категории", style = MaterialTheme.typography.headlineSmall)
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(bottom = 12.dp)) {
            FilterChipLike(text = "Расходы", selected = selectedType == "expense") { selectedType = "expense" }
            FilterChipLike(text = "Доходы", selected = selectedType == "income") { selectedType = "income" }
        }

        LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(list, key = { it.id }) { category ->
                CategoryRow(category = category, onDelete = { viewModel.delete(category) })
            }
            item {
                AddRow(
                    label = if (selectedType == "expense") "Добавить категорию расходов…" else "Добавить категорию доходов…",
                    onClick = { showAddDialog = true },
                )
            }
        }
    }

    if (showAddDialog) {
        SimpleAddDialog(
            title = "Новая категория",
            confirmEnabled = newName.isNotBlank(),
            onDismiss = { showAddDialog = false; newName = ""; newIcon = "📦" },
            onConfirm = {
                viewModel.addCategory(newName, selectedType, newIcon, nextColorSlot)
                showAddDialog = false
                newName = ""
                newIcon = "📦"
            },
        ) {
            OutlinedTextField(value = newIcon, onValueChange = { if (it.length <= 2) newIcon = it }, label = { Text("Эмодзи-иконка") })
            OutlinedTextField(value = newName, onValueChange = { newName = it }, label = { Text("Название") })
        }
    }
}

@Composable
private fun CategoryRow(category: Category, onDelete: () -> Unit) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .background(color = iconBackgroundForSlot(category.colorSlot), shape = RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Text(category.icon)
            }
            Text(
                category.name,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f).padding(start = 12.dp),
            )
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Close, contentDescription = "Удалить", tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
