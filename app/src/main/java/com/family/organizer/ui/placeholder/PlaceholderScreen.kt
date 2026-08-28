package com.family.organizer.ui.placeholder

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Заглушка для экранов, которые ещё не реализованы (Дашборд, Финансы,
 * Задачи, Ещё). Полностью рабочий пример — экран "Покупки" (см. ui/shopping) —
 * по нему собираются остальные модули, см. следующие шаги в README.md.
 */
@Composable
fun PlaceholderScreen(title: String, subtitle: String) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(modifier = Modifier.padding(top = 80.dp)) {
            Text(title, style = MaterialTheme.typography.headlineSmall)
            Text(
                subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp),
            )
        }
    }
}
