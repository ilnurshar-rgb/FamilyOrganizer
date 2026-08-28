package com.family.organizer.ui.common

import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

/**
 * Кнопка «назад» в виде текстового «‹» — как .back-btn в family-app-mockup.html.
 * Сознательно не используем Icons.Default.ArrowBack: он не гарантированно входит
 * в material-icons-core (мы не можем собрать проект локально, чтобы проверить —
 * см. README.md), а текстовый символ работает всегда без лишней зависимости.
 */
@Composable
fun BackButton(onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Text("‹", style = MaterialTheme.typography.headlineSmall)
    }
}
