package com.family.organizer.ui.shopping

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.family.organizer.data.ShoppingItem

@Composable
fun ShoppingScreen(viewModel: ShoppingViewModel) {
    val items by viewModel.items.collectAsState()
    var newItemText by remember { mutableStateOf("") }

    val notBought = items.filter { !it.isBought }
    val bought = items.filter { it.isBought }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
    ) {
        Column(modifier = Modifier.padding(top = 16.dp, bottom = 12.dp)) {
            Text("Покупки", style = MaterialTheme.typography.headlineSmall)
            Text(
                "${notBought.size} товаров в списке",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        OutlinedTextField(
            value = newItemText,
            onValueChange = { newItemText = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Добавить товар…") },
            trailingIcon = {
                IconButton(onClick = {
                    if (newItemText.isNotBlank()) {
                        viewModel.addItem(newItemText)
                        newItemText = ""
                    }
                }) {
                    Icon(Icons.Default.Add, contentDescription = "Добавить")
                }
            },
            singleLine = true,
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(top = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            if (notBought.isEmpty() && bought.isEmpty()) {
                item {
                    Text(
                        "Список пуст — добавьте первый товар выше",
                        modifier = Modifier.padding(top = 24.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            items(notBought, key = { it.id }) { item ->
                ShoppingRow(item = item, onToggle = { viewModel.toggleBought(item) }, onDelete = { viewModel.delete(item) })
            }

            if (bought.isNotEmpty()) {
                item {
                    Text(
                        "Куплено (${bought.size})",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 12.dp, bottom = 4.dp),
                    )
                }
                items(bought, key = { it.id }) { item ->
                    ShoppingRow(item = item, onToggle = { viewModel.toggleBought(item) }, onDelete = { viewModel.delete(item) })
                }
            }
        }
    }
}

@Composable
private fun ShoppingRow(item: ShoppingItem, onToggle: () -> Unit, onDelete: () -> Unit) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clickable(onClick = onToggle)
                    .background(
                        color = if (item.isBought) MaterialTheme.colorScheme.primary else Color.Transparent,
                        shape = CircleShape,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                if (item.isBought) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                }
            }

            Column(modifier = Modifier.weight(1f).padding(start = 12.dp)) {
                Text(
                    item.title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (item.isBought) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
                    textDecoration = if (item.isBought) TextDecoration.LineThrough else null,
                )
                Text(item.category, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Close, contentDescription = "Удалить", tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
