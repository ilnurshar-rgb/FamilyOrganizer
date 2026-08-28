package com.family.organizer.ui.finance

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import com.family.organizer.data.MoneyTransaction
import com.family.organizer.data.SavingsAccount
import com.family.organizer.ui.common.AddRow
import com.family.organizer.ui.common.SimpleAddDialog
import com.family.organizer.ui.common.formatDateLabel
import com.family.organizer.ui.common.formatMoney
import com.family.organizer.ui.common.iconBackgroundForSlot
import com.family.organizer.ui.common.isCurrentMonth

@Composable
fun FinanceScreen(
    viewModel: FinanceViewModel,
    onAddTransaction: () -> Unit,
) {
    val transactions by viewModel.transactions.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val savingsAccounts by viewModel.savingsAccounts.collectAsState()
    val familyMembers by viewModel.familyMembers.collectAsState()

    val monthTransactions = transactions.filter { isCurrentMonth(it.createdAt) }
    val totalIncome = monthTransactions.filter { it.type == "income" }.sumOf { it.amount }
    val totalExpense = monthTransactions.filter { it.type == "expense" }.sumOf { it.amount }
    val totalSaved = monthTransactions.filter { it.type == "saving" }.sumOf { it.amount }
    val balance = transactions.filter { it.type == "income" }.sumOf { it.amount } -
        transactions.filter { it.type == "expense" }.sumOf { it.amount } -
        transactions.filter { it.type == "saving" }.sumOf { it.amount }

    val categoryById = categories.associateBy { it.id }
    val memberById = familyMembers.associateBy { it.id }

    val expenseByCategory = monthTransactions.filter { it.type == "expense" }
        .groupBy { it.categoryId }
        .map { (categoryId, list) -> categoryById[categoryId] to list.sumOf { it.amount } }
        .filter { it.first != null }
        .sortedByDescending { it.second }
    val maxCategoryAmount = expenseByCategory.maxOfOrNull { it.second } ?: 0.0

    var showAddSavingsDialog by remember { mutableStateOf(false) }
    var newSavingsTitle by remember { mutableStateOf("") }
    var newSavingsIcon by remember { mutableStateOf("🏦") }
    var newSavingsTarget by remember { mutableStateOf("") }

    var contributingAccount by remember { mutableStateOf<SavingsAccount?>(null) }
    var contributeAmount by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Финансы", style = MaterialTheme.typography.headlineSmall)
                    Text(
                        "Баланс и операции семьи",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                IconButton(onClick = onAddTransaction) {
                    Icon(Icons.Default.Add, contentDescription = "Добавить операцию")
                }
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text("Баланс семьи", color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f), style = MaterialTheme.typography.labelLarge)
                    Text(formatMoney(balance), color = MaterialTheme.colorScheme.onPrimary, style = MaterialTheme.typography.headlineMedium)
                }
            }
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                StatTile(label = "Доходы", value = formatMoney(totalIncome), modifier = Modifier.weight(1f))
                StatTile(label = "Расходы", value = formatMoney(totalExpense), modifier = Modifier.weight(1f))
                StatTile(label = "Отложено", value = formatMoney(totalSaved), modifier = Modifier.weight(1f))
            }
        }

        item {
            Card(shape = RoundedCornerShape(18.dp), modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Накопления", style = MaterialTheme.typography.titleMedium)
                        Text(formatMoney(savingsAccounts.sumOf { it.currentAmount }), style = MaterialTheme.typography.titleMedium)
                    }
                    savingsAccounts.forEach { account ->
                        SavingsRow(account = account, onClick = { contributingAccount = account; contributeAmount = "" })
                    }
                    Box(modifier = Modifier.padding(top = 8.dp)) {
                        AddRow(label = "Добавить копилку…", onClick = { showAddSavingsDialog = true })
                    }
                }
            }
        }

        if (expenseByCategory.isNotEmpty()) {
            item {
                Card(shape = RoundedCornerShape(18.dp), modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Расходы по категориям", style = MaterialTheme.typography.titleMedium)
                        expenseByCategory.forEach { (category, amount) ->
                            if (category != null) {
                                CategoryBar(name = category.name, amount = amount, fraction = if (maxCategoryAmount > 0) (amount / maxCategoryAmount).toFloat() else 0f)
                            }
                        }
                    }
                }
            }
        }

        item {
            Card(shape = RoundedCornerShape(18.dp), modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Последние операции", style = MaterialTheme.typography.titleMedium)
                    if (transactions.isEmpty()) {
                        Text(
                            "Операций пока нет — добавьте первую через «+»",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 8.dp),
                        )
                    }
                    transactions.take(5).forEach { transaction ->
                        TransactionRow(
                            transaction = transaction,
                            categoryName = categoryById[transaction.categoryId]?.name ?: if (transaction.type == "saving") "Накопления" else "Прочее",
                            categoryIcon = categoryById[transaction.categoryId]?.icon ?: "🏦",
                            colorSlot = categoryById[transaction.categoryId]?.colorSlot ?: 1,
                            authorName = memberById[transaction.authorId]?.name,
                        )
                    }
                }
            }
        }

        item { Box(modifier = Modifier.height(8.dp)) }
    }

    if (showAddSavingsDialog) {
        SimpleAddDialog(
            title = "Новая копилка",
            confirmEnabled = newSavingsTitle.isNotBlank(),
            onDismiss = { showAddSavingsDialog = false; newSavingsTitle = ""; newSavingsIcon = "🏦"; newSavingsTarget = "" },
            onConfirm = {
                viewModel.addSavingsAccount(
                    newSavingsTitle,
                    newSavingsIcon,
                    (savingsAccounts.size % 8) + 1,
                    newSavingsTarget.replace(",", ".").toDoubleOrNull(),
                )
                showAddSavingsDialog = false
                newSavingsTitle = ""
                newSavingsIcon = "🏦"
                newSavingsTarget = ""
            },
        ) {
            OutlinedTextField(value = newSavingsIcon, onValueChange = { if (it.length <= 2) newSavingsIcon = it }, label = { Text("Эмодзи-иконка") })
            OutlinedTextField(value = newSavingsTitle, onValueChange = { newSavingsTitle = it }, label = { Text("Название") })
            OutlinedTextField(
                value = newSavingsTarget,
                onValueChange = { value -> if (value.all { it.isDigit() || it == '.' || it == ',' }) newSavingsTarget = value },
                label = { Text("Цель, ₽ (необязательно)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            )
        }
    }

    val accountToContribute = contributingAccount
    if (accountToContribute != null) {
        SimpleAddDialog(
            title = "Отложить в «${accountToContribute.title}»",
            confirmLabel = "Отложить",
            confirmEnabled = contributeAmount.replace(",", ".").toDoubleOrNull() != null,
            onDismiss = { contributingAccount = null },
            onConfirm = {
                val value = contributeAmount.replace(",", ".").toDoubleOrNull()
                if (value != null && value > 0.0) {
                    viewModel.contributeToSavings(accountToContribute, value)
                }
                contributingAccount = null
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
private fun StatTile(label: String, value: String, modifier: Modifier = Modifier) {
    Card(shape = RoundedCornerShape(16.dp), modifier = modifier) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Composable
private fun SavingsRow(account: SavingsAccount, onClick: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(vertical = 8.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(30.dp).background(iconBackgroundForSlot(account.colorSlot), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text(account.icon, style = MaterialTheme.typography.labelLarge)
            }
            Text(account.title, modifier = Modifier.weight(1f).padding(start = 10.dp), style = MaterialTheme.typography.bodyMedium)
            Text(formatMoney(account.currentAmount), style = MaterialTheme.typography.bodyMedium)
        }
        val target = account.targetAmount
        if (target != null && target > 0.0) {
            val fraction = (account.currentAmount / target).toFloat().coerceIn(0f, 1f)
            Box(
                modifier = Modifier
                    .padding(top = 6.dp, start = 40.dp)
                    .fillMaxWidth()
                    .height(6.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(3.dp)),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(fraction)
                        .height(6.dp)
                        .background(iconBackgroundForSlot(account.colorSlot).copy(alpha = 1f), RoundedCornerShape(3.dp)),
                )
            }
        }
    }
}

@Composable
private fun CategoryBar(name: String, amount: Double, fraction: Float) {
    Column(modifier = Modifier.fillMaxWidth().padding(top = 10.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(name, style = MaterialTheme.typography.bodyMedium)
            Text(formatMoney(amount), style = MaterialTheme.typography.bodyMedium)
        }
        Box(
            modifier = Modifier
                .padding(top = 4.dp)
                .fillMaxWidth()
                .height(6.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(3.dp)),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction.coerceIn(0f, 1f))
                    .height(6.dp)
                    .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(3.dp)),
            )
        }
    }
}

@Composable
private fun TransactionRow(
    transaction: MoneyTransaction,
    categoryName: String,
    categoryIcon: String,
    colorSlot: Int,
    authorName: String?,
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier.size(32.dp).background(iconBackgroundForSlot(colorSlot), RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Text(categoryIcon)
        }
        Column(modifier = Modifier.weight(1f).padding(start = 10.dp)) {
            Text(categoryName, style = MaterialTheme.typography.bodyMedium)
            Text(
                listOfNotNull(authorName, formatDateLabel(transaction.createdAt)).joinToString(" · "),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        val sign = if (transaction.type == "income") "+" else "−"
        Text(
            "$sign${formatMoney(transaction.amount).removePrefix("−")}",
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}
