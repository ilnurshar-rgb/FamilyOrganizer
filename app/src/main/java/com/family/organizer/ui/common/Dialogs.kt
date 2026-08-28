package com.family.organizer.ui.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

/**
 * Общий каркас для коротких форм добавления (категория, копилка, цель,
 * задача, событие, желание) — вместо отдельного экрана на каждую форму.
 * Сознательное упрощение первой версии: меньше экранов и навигации,
 * ниже риск ошибок в CI-сборке (см. README.md).
 */
@Composable
fun SimpleAddDialog(
    title: String,
    confirmLabel: String = "Добавить",
    confirmEnabled: Boolean = true,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    fields: @Composable () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                fields()
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm, enabled = confirmEnabled) {
                Text(confirmLabel)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        },
    )
}
