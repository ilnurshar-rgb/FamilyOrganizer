package com.family.organizer.ui.dashboard

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.family.organizer.ui.common.formatEpochDayLabel
import com.family.organizer.ui.common.formatMoney
import com.family.organizer.ui.common.isCurrentMonth
import java.time.LocalDate

@Composable
fun DashboardScreen(viewModel: DashboardViewModel, onNavigate: (String) -> Unit) {
    val transactions by viewModel.transactions.collectAsState()
    val goals by viewModel.goals.collectAsState()
    val tasks by viewModel.tasks.collectAsState()
    val shoppingItems by viewModel.shoppingItems.collectAsState()
    val events by viewModel.events.collectAsState()

    val monthTransactions = transactions.filter { isCurrentMonth(it.createdAt) }
    val monthIncome = monthTransactions.filter { it.type == "income" }.sumOf { it.amount }
    val monthExpense = monthTransactions.filter { it.type == "expense" }.sumOf { it.amount }
    val balance = transactions.filter { it.type == "income" }.sumOf { it.amount } -
        transactions.filter { it.type == "expense" }.sumOf { it.amount } -
        transactions.filter { it.type == "saving" }.sumOf { it.amount }

    val todayTasks = tasks.filter { it.dueBucket == "today" }
    val shoppingLeft = shoppingItems.count { !it.isBought }
    val today = LocalDate.now()
    val upcomingEvents = events.filter { it.dateEpochDay >= today.toEpochDay() }.sortedBy { it.dateEpochDay }.take(3)

    LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item {
            Column(modifier = Modifier.padding(top = 16.dp)) {
                Text("Привет!", style = MaterialTheme.typography.headlineSmall)
                Text(
                    "Вот что происходит у вашей семьи сегодня",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                QuickAction(label = "+ Покупка", modifier = Modifier.weight(1f), onClick = { onNavigate("shopping") })
                QuickAction(label = "+ Задача", modifier = Modifier.weight(1f), onClick = { onNavigate("tasks") })
                QuickAction(label = "+ Расход", modifier = Modifier.weight(1f), onClick = { onNavigate("finance_add") })
            }
        }

        item {
            DashboardCard(title = "Финансы", onClick = { onNavigate("finance") }) {
                Text(formatMoney(balance), style = MaterialTheme.typography.headlineSmall)
                Text(
                    "Доходы за месяц: ${formatMoney(monthIncome)} · Расходы: ${formatMoney(monthExpense)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
        }

        if (goals.isNotEmpty()) {
            item {
                DashboardCard(title = "Ближайшие цели", onClick = { onNavigate("more_goals") }) {
                    goals.take(2).forEach { goal ->
                        val target = goal.targetAmount
                        val percent = if (target != null && target > 0.0) ((goal.currentAmount / target) * 100).toInt() else null
                        Row(modifier = Modifier.fillMaxWidth().padding(top = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("${goal.icon} ${goal.title}", style = MaterialTheme.typography.bodyMedium)
                            if (percent != null) Text("$percent%", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }

        item {
            DashboardCard(title = "Задачи на сегодня", onClick = { onNavigate("tasks") }) {
                Text(
                    "${todayTasks.count { it.isDone }} из ${todayTasks.size} выполнено",
                    style = MaterialTheme.typography.bodyMedium,
                )
                Text(
                    "$shoppingLeft товаров осталось купить",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 2.dp),
                )
            }
        }

        item {
            DashboardCard(title = "Ближайшие события", onClick = { onNavigate("more_calendar") }) {
                if (upcomingEvents.isEmpty()) {
                    Text(
                        "Событий пока нет",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                upcomingEvents.forEach { event ->
                    Text(
                        "${event.title} — ${formatEpochDayLabel(event.dateEpochDay)}",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 4.dp),
                    )
                }
            }
        }

        item { Box(modifier = Modifier.height(8.dp)) }
    }
}

@Composable
private fun QuickAction(label: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
        modifier = modifier.clickable(onClick = onClick),
    ) {
        Box(modifier = Modifier.padding(vertical = 12.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
            Text(label, color = MaterialTheme.colorScheme.onPrimary, style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
private fun DashboardCard(title: String, onClick: () -> Unit, content: @Composable () -> Unit) {
    Card(shape = RoundedCornerShape(18.dp), modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 6.dp))
            content()
        }
    }
}
