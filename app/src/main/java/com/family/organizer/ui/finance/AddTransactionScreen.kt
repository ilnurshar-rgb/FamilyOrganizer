package com.family.organizer.ui.finance

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.family.organizer.data.Category
import com.family.organizer.ui.common.BackButton
import com.family.organizer.ui.common.FilterChipLike
import com.family.organizer.ui.common.iconBackgroundForSlot

@Composable
fun AddTransactionScreen(
    viewModel: AddTransactionViewModel,
    onDone: () -> Unit,
    onBack: () -> Unit,
    onAddCategory: () -> Unit,
) {
    val expenseCategories by viewModel.expenseCategories.collectAsState()
    val incomeCategories by viewModel.incomeCategories.collectAsState()

    var type by remember { mutableStateOf("expense") }
    var amountText by remember { mutableStateOf("") }
    var selectedCategoryId by remember { mutableStateOf<String?>(null) }

    val categories = if (type == "expense") expenseCategories else incomeCategories
    val amount = amountText.replace(",", ".").toDoubleOrNull()
    val canSubmit = amount != null && amount > 0.0 && selectedCategoryId != null

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BackButton(onClick = onBack)
            Text("Новая операция", style = MaterialTheme.typography.headlineSmall)
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(bottom = 16.dp)) {
            FilterChipLike(text = "Расход", selected = type == "expense") {
                type = "expense"
                selectedCategoryId = null
            }
            FilterChipLike(text = "Доход", selected = type == "income") {
                type = "income"
                selectedCategoryId = null
            }
        }

        OutlinedTextField(
            value = amountText,
            onValueChange = { value -> if (value.all { it.isDigit() || it == '.' || it == ',' }) amountText = value },
            label = { Text("Сумма, ₽") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth(),
        )

        Text(
            "Категория",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 20.dp, bottom = 8.dp),
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            modifier = Modifier.fillMaxWidth().weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(categories, key = { it.id }) { category ->
                CategoryTile(
                    category = category,
                    selected = category.id == selectedCategoryId,
                    onClick = { selectedCategoryId = category.id },
                )
            }
            item { AddCategoryTile(onClick = onAddCategory) }
        }

        Button(
            onClick = {
                val value = amount
                val categoryId = selectedCategoryId
                if (value != null && categoryId != null) {
                    viewModel.addTransaction(value, type, categoryId, onDone)
                }
            },
            enabled = canSubmit,
            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
        ) {
            Text("Добавить операцию")
        }
    }
}

@Composable
private fun CategoryTile(category: Category, selected: Boolean, onClick: () -> Unit) {
    Column(
        modifier = Modifier.clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .background(color = iconBackgroundForSlot(category.colorSlot), shape = RoundedCornerShape(16.dp))
                .border(
                    width = if (selected) 2.dp else 0.dp,
                    color = if (selected) MaterialTheme.colorScheme.primary else Color.Transparent,
                    shape = RoundedCornerShape(16.dp),
                ),
            contentAlignment = Alignment.Center,
        ) {
            Text(category.icon, style = MaterialTheme.typography.titleLarge)
        }
        Text(
            category.name,
            style = MaterialTheme.typography.labelSmall,
            color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp),
        )
    }
}

@Composable
private fun AddCategoryTile(onClick: () -> Unit) {
    Column(
        modifier = Modifier.clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .background(color = MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Text("+", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Text(
            "Добавить",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp),
        )
    }
}
