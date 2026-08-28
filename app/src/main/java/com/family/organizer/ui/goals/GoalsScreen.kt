package com.family.organizer.ui.goals

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
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
import com.family.organizer.data.Goal
import com.family.organizer.ui.common.BackButton
import com.family.organizer.ui.common.SimpleAddDialog
import com.family.organizer.ui.common.formatMoney

@Composable
fun GoalsScreen(viewModel: GoalsViewModel, onBack: () -> Unit) {
    val goals by viewModel.goals.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf("") }
    var target by remember { mutableStateOf("") }
    var deadline by remember { mutableStateOf("") }
    var contributors by remember { mutableStateOf("") }

    var contributingGoal by remember { mutableStateOf<Goal?>(null) }
    var contributeAmount by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BackButton(onClick = onBack)
            Text("Цели", style = MaterialTheme.typography.headlineSmall, modifier = Modifier.weight(1f))
            IconButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Добавить цель")
            }
        }

        LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            if (goals.isEmpty()) {
                item {
                    Text(
                        "Пока нет целей — добавьте первую через «+»",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 16.dp),
                    )
                }
            }
            items(goals, key = { it.id }) { goal ->
                GoalCard(goal = goal, onClick = { contributingGoal = goal; contributeAmount = "" })
            }
            item { Box(modifier = Modifier.height(8.dp)) }
        }
    }

    if (showAddDialog) {
        SimpleAddDialog(
            title = "Новая цель",
            confirmEnabled = title.isNotBlank(),
            onDismiss = { showAddDialog = false; title = ""; target = ""; deadline = ""; contributors = "" },
            onConfirm = {
                viewModel.addGoal(title, "🎯", (goals.size % 8) + 1, target.replace(",", ".").toDoubleOrNull(), deadline, contributors)
                showAddDialog = false
                title = ""; target = ""; deadline = ""; contributors = ""
            },
        ) {
            OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Название цели") })
            OutlinedTextField(
                value = target,
                onValueChange = { value -> if (value.all { it.isDigit() || it == '.' || it == ',' }) target = value },
                label = { Text("Целевая сумма, ₽ (необязательно)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            )
            OutlinedTextField(value = deadline, onValueChange = { deadline = it }, label = { Text("Срок (например «июль 2027»)") })
            OutlinedTextField(value = contributors, onValueChange = { contributors = it }, label = { Text("Кто копит (например «копят все»)") })
        }
    }

    val goalToContribute = contributingGoal
    if (goalToContribute != null) {
        SimpleAddDialog(
            title = "Пополнить «${goalToContribute.title}»",
            confirmLabel = "Пополнить",
            confirmEnabled = contributeAmount.replace(",", ".").toDoubleOrNull() != null,
            onDismiss = { contributingGoal = null },
            onConfirm = {
                val value = contributeAmount.replace(",", ".").toDoubleOrNull()
                if (value != null && value > 0.0) viewModel.contribute(goalToContribute, value)
                contributingGoal = null
            },
        ) {
            OutlinedTextField(
                value = contributeAmount,
                onValueChange = { value -> if (value.all { it.isDigit() || it == '.' || it == ',' }) contributeAmount = value },
                label = { Text("Сумма, ₽") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            )
        }
    }
}

@Composable
private fun GoalCard(goal: Goal, onClick: () -> Unit) {
    Card(shape = RoundedCornerShape(18.dp), modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("${goal.icon} ${goal.title}", style = MaterialTheme.typography.titleMedium)
                val target = goal.targetAmount
                if (target != null && target > 0.0) {
                    val percent = ((goal.currentAmount / target) * 100).toInt().coerceIn(0, 999)
                    Text("$percent%", style = MaterialTheme.typography.titleMedium)
                }
            }

            val target = goal.targetAmount
            if (target != null && target > 0.0) {
                val fraction = (goal.currentAmount / target).toFloat().coerceIn(0f, 1f)
                Box(
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .fillMaxWidth()
                        .height(8.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(4.dp)),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(fraction)
                            .height(8.dp)
                            .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(4.dp)),
                    )
                }
                Row(modifier = Modifier.fillMaxWidth().padding(top = 6.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(formatMoney(goal.currentAmount), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("из ${formatMoney(target)}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                Text(
                    "Накоплено: ${formatMoney(goal.currentAmount)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }

            val subtitle = listOfNotNull(
                goal.deadlineLabel?.let { "Дедлайн: $it" } ?: "Без срока",
                goal.contributorsLabel,
            ).joinToString(" · ")
            Text(subtitle, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 8.dp))
        }
    }
}
