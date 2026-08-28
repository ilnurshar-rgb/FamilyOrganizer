package com.family.organizer.ui.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
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
import com.family.organizer.data.CalendarEvent
import com.family.organizer.ui.common.AddRow
import com.family.organizer.ui.common.BackButton
import com.family.organizer.ui.common.FilterChipLike
import com.family.organizer.ui.common.SimpleAddDialog
import com.family.organizer.ui.common.colorForSlot
import java.time.LocalDate
import java.time.YearMonth

private val MONTH_NAMES = listOf(
    "Январь", "Февраль", "Март", "Апрель", "Май", "Июнь",
    "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь",
)
private val MONTH_SHORT = listOf(
    "янв", "фев", "мар", "апр", "май", "июн", "июл", "авг", "сен", "окт", "ноя", "дек",
)
private val WEEKDAY_HEADERS = listOf("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс")
private val EVENT_TYPES = listOf("family" to "Семейное", "birthday" to "День рождения", "personal" to "Личное", "other" to "Другое")

private fun colorSlotForType(type: String): Int = when (type) {
    "family" -> 1
    "birthday" -> 5
    "personal" -> 3
    else -> 2
}

private fun buildWeeks(month: YearMonth): List<List<LocalDate>> {
    val firstOfMonth = month.atDay(1)
    val leadingDays = firstOfMonth.dayOfWeek.value - 1 // Monday=1 -> 0 leading
    val totalCells = ((leadingDays + month.lengthOfMonth() + 6) / 7) * 7
    val startDate = firstOfMonth.minusDays(leadingDays.toLong())
    return (0 until totalCells).map { startDate.plusDays(it.toLong()) }.chunked(7)
}

@Composable
fun CalendarScreen(viewModel: CalendarViewModel, onBack: () -> Unit) {
    val events by viewModel.events.collectAsState()
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }

    var showAddDialog by remember { mutableStateOf(false) }
    var newTitle by remember { mutableStateOf("") }
    var newType by remember { mutableStateOf("family") }
    var newDate by remember { mutableStateOf(LocalDate.now()) }
    var newTime by remember { mutableStateOf("") }
    var newYearly by remember { mutableStateOf(false) }

    val today = LocalDate.now()
    val eventsByDate = events.groupBy { LocalDate.ofEpochDay(it.dateEpochDay) }
    val weeks = buildWeeks(currentMonth)
    val upcoming = events.filter { it.dateEpochDay >= today.toEpochDay() }.sortedBy { it.dateEpochDay }.take(10)

    LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                BackButton(onClick = onBack)
                Text("Календарь", style = MaterialTheme.typography.headlineSmall)
            }
        }

        item {
            Card(shape = RoundedCornerShape(18.dp), modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        IconButton(onClick = { currentMonth = currentMonth.minusMonths(1) }) { Text("‹", style = MaterialTheme.typography.titleLarge) }
                        Text("${MONTH_NAMES[currentMonth.monthValue - 1]} ${currentMonth.year}", style = MaterialTheme.typography.titleMedium)
                        IconButton(onClick = { currentMonth = currentMonth.plusMonths(1) }) { Text("›", style = MaterialTheme.typography.titleLarge) }
                    }

                    Row(modifier = Modifier.fillMaxWidth()) {
                        WEEKDAY_HEADERS.forEach { label ->
                            Text(
                                label,
                                modifier = Modifier.weight(1f),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }

                    weeks.forEach { week ->
                        Row(modifier = Modifier.fillMaxWidth()) {
                            week.forEach { date ->
                                DayCell(
                                    date = date,
                                    inCurrentMonth = YearMonth.from(date) == currentMonth,
                                    isToday = date == today,
                                    dotSlots = eventsByDate[date]?.map { colorSlotForType(it.type) }?.distinct().orEmpty(),
                                    modifier = Modifier.weight(1f),
                                )
                            }
                        }
                    }

                    Row(modifier = Modifier.fillMaxWidth().padding(top = 10.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        LegendDot(slot = 1, label = "Семейное")
                        LegendDot(slot = 5, label = "ДР")
                        LegendDot(slot = 2, label = "Задача")
                        LegendDot(slot = 3, label = "Личное")
                    }
                }
            }
        }

        item { AddRow(label = "Добавить событие…", onClick = { showAddDialog = true }) }

        item {
            Card(shape = RoundedCornerShape(18.dp), modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Ближайшие события", style = MaterialTheme.typography.titleMedium)
                    if (upcoming.isEmpty()) {
                        Text(
                            "Событий пока нет",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 8.dp),
                        )
                    }
                    upcoming.forEach { event -> AgendaRow(event = event) }
                }
            }
        }

        item { Box(modifier = Modifier.height(8.dp)) }
    }

    if (showAddDialog) {
        SimpleAddDialog(
            title = "Новое событие",
            confirmEnabled = newTitle.isNotBlank(),
            onDismiss = { showAddDialog = false; newTitle = ""; newTime = ""; newYearly = false },
            onConfirm = {
                viewModel.addEvent(
                    title = newTitle,
                    type = newType,
                    dateEpochDay = newDate.toEpochDay(),
                    timeLabel = newTime.ifBlank { null },
                    allDay = newTime.isBlank(),
                    recurrence = if (newYearly) "yearly" else "none",
                )
                showAddDialog = false; newTitle = ""; newTime = ""; newYearly = false
            },
        ) {
            OutlinedTextField(value = newTitle, onValueChange = { newTitle = it }, label = { Text("Название события") })

            Text("Тип", style = MaterialTheme.typography.labelMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                EVENT_TYPES.forEach { (value, label) ->
                    FilterChipLike(text = label, selected = newType == value) { newType = value }
                }
            }

            Text("Дата", style = MaterialTheme.typography.labelMedium)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items((0..44).map { today.plusDays(it.toLong()) }) { date ->
                    FilterChipLike(text = "${date.dayOfMonth} ${MONTH_SHORT[date.monthValue - 1]}", selected = date == newDate) { newDate = date }
                }
            }

            OutlinedTextField(
                value = newTime,
                onValueChange = { newTime = it },
                label = { Text("Время, необязательно (например 18:00)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            )

            FilterChipLike(text = if (newYearly) "Повторяется ежегодно ✓" else "Повторяется ежегодно?", selected = newYearly) {
                newYearly = !newYearly
            }
        }
    }
}

@Composable
private fun DayCell(date: LocalDate, inCurrentMonth: Boolean, isToday: Boolean, dotSlots: List<Int>, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(2.dp).aspectRatio(1f),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .background(
                    color = if (isToday) MaterialTheme.colorScheme.primary else androidx.compose.ui.graphics.Color.Transparent,
                    shape = CircleShape,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                date.dayOfMonth.toString(),
                style = MaterialTheme.typography.labelMedium,
                color = when {
                    isToday -> MaterialTheme.colorScheme.onPrimary
                    !inCurrentMonth -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                    else -> MaterialTheme.colorScheme.onSurface
                },
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(2.dp), modifier = Modifier.padding(top = 2.dp)) {
            dotSlots.take(3).forEach { slot ->
                Box(modifier = Modifier.size(4.dp).background(colorForSlot(slot), CircleShape))
            }
        }
    }
}

@Composable
private fun LegendDot(slot: Int, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(6.dp).background(colorForSlot(slot), CircleShape))
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(start = 4.dp))
    }
}

@Composable
private fun AgendaRow(event: CalendarEvent) {
    val date = LocalDate.ofEpochDay(event.dateEpochDay)
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.size(40.dp)) {
            Text(date.dayOfMonth.toString(), style = MaterialTheme.typography.titleMedium)
            Text(MONTH_SHORT[date.monthValue - 1], style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Box(modifier = Modifier.padding(horizontal = 10.dp).size(width = 3.dp, height = 32.dp).background(colorForSlot(colorSlotForType(event.type)), RoundedCornerShape(2.dp)))
        Column(modifier = Modifier.weight(1f)) {
            Text(event.title, style = MaterialTheme.typography.bodyMedium)
            val meta = listOfNotNull(
                if (event.allDay) "весь день" else event.timeLabel,
                if (event.recurrence == "yearly") "повторяется ежегодно" else null,
                event.participantsLabel,
            ).joinToString(" · ")
            Text(meta, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
