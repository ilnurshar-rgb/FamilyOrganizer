package com.family.organizer.ui.tasks

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
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.unit.dp
import com.family.organizer.data.TaskItem
import com.family.organizer.ui.common.FilterChipLike
import com.family.organizer.ui.common.SimpleAddDialog

@Composable
fun TasksScreen(viewModel: TasksViewModel) {
    val tasks by viewModel.tasks.collectAsState()
    val members by viewModel.familyMembers.collectAsState()
    val memberById = members.associateBy { it.id }

    var filterMemberId by remember { mutableStateOf<String?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }
    var newTitle by remember { mutableStateOf("") }
    var newAssigneeId by remember { mutableStateOf<String?>(null) }
    var newBucket by remember { mutableStateOf("today") }
    var newRecurring by remember { mutableStateOf(false) }

    val visibleTasks = if (filterMemberId == null) tasks else tasks.filter { it.assignedToId == filterMemberId }
    val doneCount = visibleTasks.count { it.isDone }

    val todayTasks = visibleTasks.filter { it.dueBucket == "today" }
    val weekTasks = visibleTasks.filter { it.dueBucket == "week" }
    val noDateTasks = visibleTasks.filter { it.dueBucket != "today" && it.dueBucket != "week" }

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Задачи", style = MaterialTheme.typography.headlineSmall)
                Text(
                    "$doneCount из ${visibleTasks.size} выполнено",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            IconButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Добавить задачу")
            }
        }

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(bottom = 12.dp)) {
            item { FilterChipLike(text = "Все", selected = filterMemberId == null) { filterMemberId = null } }
            items(members, key = { it.id }) { member ->
                FilterChipLike(text = member.name, selected = filterMemberId == member.id) { filterMemberId = member.id }
            }
        }

        LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            if (todayTasks.isNotEmpty()) {
                item { GroupLabel("Сегодня") }
                items(todayTasks, key = { it.id }) { task ->
                    TaskRow(task = task, assigneeName = memberById[task.assignedToId]?.name, onToggle = { viewModel.setDone(task, !task.isDone) }, onDelete = { viewModel.delete(task) })
                }
            }
            if (weekTasks.isNotEmpty()) {
                item { GroupLabel("На неделе") }
                items(weekTasks, key = { it.id }) { task ->
                    TaskRow(task = task, assigneeName = memberById[task.assignedToId]?.name, onToggle = { viewModel.setDone(task, !task.isDone) }, onDelete = { viewModel.delete(task) })
                }
            }
            if (noDateTasks.isNotEmpty()) {
                item { GroupLabel("Без срока") }
                items(noDateTasks, key = { it.id }) { task ->
                    TaskRow(task = task, assigneeName = memberById[task.assignedToId]?.name, onToggle = { viewModel.setDone(task, !task.isDone) }, onDelete = { viewModel.delete(task) })
                }
            }
            if (visibleTasks.isEmpty()) {
                item {
                    Text(
                        "Задач пока нет — добавьте первую через «+»",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 16.dp),
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        SimpleAddDialog(
            title = "Новая задача",
            confirmEnabled = newTitle.isNotBlank(),
            onDismiss = {
                showAddDialog = false; newTitle = ""; newAssigneeId = null; newBucket = "today"; newRecurring = false
            },
            onConfirm = {
                viewModel.addTask(newTitle, newAssigneeId, newBucket, if (newRecurring) "daily" else "none")
                showAddDialog = false; newTitle = ""; newAssigneeId = null; newBucket = "today"; newRecurring = false
            },
        ) {
            OutlinedTextField(value = newTitle, onValueChange = { newTitle = it }, label = { Text("Что нужно сделать") })

            Text("Кто выполняет", style = MaterialTheme.typography.labelMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                members.forEach { member ->
                    FilterChipLike(text = member.name, selected = newAssigneeId == member.id) {
                        newAssigneeId = if (newAssigneeId == member.id) null else member.id
                    }
                }
            }

            Text("Срок", style = MaterialTheme.typography.labelMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                FilterChipLike(text = "Сегодня", selected = newBucket == "today") { newBucket = "today" }
                FilterChipLike(text = "На неделе", selected = newBucket == "week") { newBucket = "week" }
                FilterChipLike(text = "Без срока", selected = newBucket == "none") { newBucket = "none" }
            }

            FilterChipLike(text = if (newRecurring) "Повторяется ежедневно ✓" else "Повторяется ежедневно?", selected = newRecurring) {
                newRecurring = !newRecurring
            }
        }
    }
}

@Composable
private fun GroupLabel(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(top = 10.dp, bottom = 4.dp),
    )
}

@Composable
private fun TaskRow(task: TaskItem, assigneeName: String?, onToggle: () -> Unit, onDelete: () -> Unit) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clickable(onClick = onToggle)
                    .background(
                        color = if (task.isDone) MaterialTheme.colorScheme.primary else Color.Transparent,
                        shape = CircleShape,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                if (task.isDone) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                }
            }

            Column(modifier = Modifier.weight(1f).padding(start = 12.dp)) {
                Text(
                    task.title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (task.isDone) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
                )
                if (assigneeName != null) {
                    Text(assigneeName, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            if (task.recurrence == "daily") {
                Text(
                    "Ежедн.",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(end = 8.dp),
                )
            }

            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Close, contentDescription = "Удалить", tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
