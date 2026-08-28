package com.family.organizer.ui.more

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.family.organizer.ui.common.colorForSlot
import java.time.LocalDate

@Composable
fun MoreScreen(
    viewModel: MoreViewModel,
    onOpenCalendar: () -> Unit,
    onOpenGoals: () -> Unit,
    onOpenWishlist: () -> Unit,
    onOpenCategories: () -> Unit,
    onSignOut: () -> Unit,
) {
    val members by viewModel.familyMembers.collectAsState()
    val events by viewModel.events.collectAsState()
    val goals by viewModel.goals.collectAsState()
    val wishlistItems by viewModel.wishlistItems.collectAsState()

    val today = LocalDate.now()
    val weekAhead = today.plusDays(7)
    val eventsThisWeek = events.count { event ->
        val date = LocalDate.ofEpochDay(event.dateEpochDay)
        !date.isBefore(today) && !date.isAfter(weekAhead)
    }

    LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item {
            Text("Ещё", style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(top = 16.dp, bottom = 4.dp))
        }

        item {
            MoreTile(icon = "📅", colorSlot = 1, title = "Календарь", subtitle = "$eventsThisWeek событий на этой неделе", onClick = onOpenCalendar)
        }
        item {
            MoreTile(icon = "🎯", colorSlot = 3, title = "Цели", subtitle = "${goals.size} активных целей", onClick = onOpenGoals)
        }
        item {
            MoreTile(icon = "★", colorSlot = 5, title = "Желания", subtitle = "${wishlistItems.size} пунктов у семьи", onClick = onOpenWishlist)
        }

        item {
            Card(shape = RoundedCornerShape(18.dp), modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Семья", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 8.dp))
                    members.forEach { member ->
                        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier.size(36.dp).background(colorForSlot(member.colorSlot), CircleShape),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(member.name.take(1), color = Color.White, style = MaterialTheme.typography.labelLarge)
                            }
                            Column(modifier = Modifier.weight(1f).padding(start = 12.dp)) {
                                Text(member.name, style = MaterialTheme.typography.bodyLarge)
                                if (member.subtitle.isNotBlank()) {
                                    Text(member.subtitle, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                            Text(
                                if (member.role == "adult") "Взрослый" else "Ребёнок",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            }
        }

        item {
            Card(shape = RoundedCornerShape(18.dp), modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Настройки", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 4.dp))
                    SettingsRow(label = "Категории доходов и расходов", onClick = onOpenCategories)
                    SettingsRow(label = "Уведомления", onClick = {})
                    SettingsRow(label = "Тёмная тема", onClick = {})
                    SettingsRow(label = "Выйти из аккаунта", onClick = onSignOut)
                }
            }
        }

        item { Box(modifier = Modifier.height(8.dp)) }
    }
}

@Composable
private fun MoreTile(icon: String, colorSlot: Int, title: String, subtitle: String, onClick: () -> Unit) {
    Card(shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)) {
        Row(modifier = Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(38.dp).background(colorForSlot(colorSlot), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Text(icon, color = Color.White)
            }
            Column(modifier = Modifier.weight(1f).padding(start = 12.dp)) {
                Text(title, style = MaterialTheme.typography.bodyLarge)
                Text(subtitle, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text("›", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun SettingsRow(label: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Text("›", color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
