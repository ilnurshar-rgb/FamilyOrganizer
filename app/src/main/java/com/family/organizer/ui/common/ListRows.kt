package com.family.organizer.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/** Строка-кнопка «+ Добавить …» — переиспользуется в Категориях, Задачах, Покупках-подобных списках. */
@Composable
fun AddRow(label: String, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
    ) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("+", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.titleMedium)
            Text(label, modifier = Modifier.padding(start = 10.dp), color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

/** Переключатель-«таблетка» (вкладки/фильтры) — как .chip в family-app-mockup.html. */
@Composable
fun FilterChipLike(text: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clickable(onClick = onClick)
            .background(
                color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(50),
            )
            .padding(horizontal = 14.dp, vertical = 8.dp),
    ) {
        Text(
            text,
            color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.labelLarge,
        )
    }
}
