package com.family.organizer.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.family.organizer.ui.common.FilterChipLike

private const val MODE_CREATE = "create"
private const val MODE_JOIN = "join"

@Composable
fun FamilySetupScreen(
    userEmail: String?,
    isLoading: Boolean,
    errorMessage: String?,
    onCreateFamily: (name: String) -> Unit,
    onJoinFamily: (inviteCode: String) -> Unit,
    onSignOut: () -> Unit,
) {
    var mode by remember { mutableStateOf(MODE_CREATE) }
    var familyName by remember { mutableStateOf("") }
    var inviteCode by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("Почти готово", style = MaterialTheme.typography.headlineSmall)
        Text(
            userEmail ?: "",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp, bottom = 20.dp),
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChipLike(text = "Создать семью", selected = mode == MODE_CREATE) { mode = MODE_CREATE }
            FilterChipLike(text = "Присоединиться", selected = mode == MODE_JOIN) { mode = MODE_JOIN }
        }

        if (mode == MODE_CREATE) {
            Text(
                "Придумайте название семьи — после создания вы получите код приглашения для остальных.",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 16.dp, bottom = 10.dp),
            )
            OutlinedTextField(
                value = familyName,
                onValueChange = { familyName = it },
                label = { Text("Название семьи") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
        } else {
            Text(
                "Введите код приглашения, который вам дал другой член семьи.",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 16.dp, bottom = 10.dp),
            )
            OutlinedTextField(
                value = inviteCode,
                onValueChange = { inviteCode = it.uppercase() },
                label = { Text("Код приглашения") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Characters),
                modifier = Modifier.fillMaxWidth(),
            )
        }

        if (errorMessage != null) {
            Text(
                errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(top = 12.dp),
            )
        }

        Button(
            onClick = {
                if (mode == MODE_CREATE) onCreateFamily(familyName) else onJoinFamily(inviteCode)
            },
            enabled = !isLoading && if (mode == MODE_CREATE) familyName.isNotBlank() else inviteCode.isNotBlank(),
            modifier = Modifier.fillMaxWidth().padding(top = 20.dp),
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
            } else {
                Text(if (mode == MODE_CREATE) "Создать семью" else "Присоединиться")
            }
        }

        TextButton(onClick = onSignOut, modifier = Modifier.padding(top = 8.dp)) {
            Text("Выйти из аккаунта")
        }
    }
}
