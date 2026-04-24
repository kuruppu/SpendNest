package com.expense.tracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.expense.tracker.data.entity.Budget
import com.expense.tracker.data.entity.Category
import com.expense.tracker.data.repository.ExpenseRepository
import com.expense.tracker.utils.BillingCycleCalculator
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetManagementScreen(
    repository: ExpenseRepository,
    onBackClick: () -> Unit
) {
    val categories by repository.getMainCategories().collectAsState(initial = emptyList())
    val budgets by repository.getAllBudgets().collectAsState(initial = emptyList())
    val preferences by repository.getUserPreferences().collectAsState(initial = null)

    var showAddBudgetDialog by remember { mutableStateOf(false) }
    var showEditBudgetDialog by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    var selectedBudget by remember { mutableStateOf<Budget?>(null) }

    val scope = rememberCoroutineScope()

    // Calculate spent amounts
    val budgetDetails = remember { mutableStateMapOf<Long, Pair<Double, Double>>() }

    LaunchedEffect(categories, budgets, preferences) {
        if (preferences != null) {
            val cycleStartDay = preferences!!.billingCycleStartDay
            val (startDate, endDate) = BillingCycleCalculator.getCurrentCycleDates(cycleStartDay)

            categories.forEach { category ->
                val spent = repository.getTotalSpentForCategory(category.id, startDate, endDate)
                val budget = budgets.find { it.categoryId == category.id }
                budgetDetails[category.id] = Pair(budget?.amount ?: 0.0, spent)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Budget Management") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddBudgetDialog = true }
            ) {
                Icon(Icons.Default.Add, "Add Budget")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = "Category Budgets",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (categories.isEmpty()) {
                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No categories available")
                        }
                    }
                }
            } else {
                items(categories) { category ->
                    val details = budgetDetails[category.id]
                    val budgetAmount = details?.first ?: 0.0
                    val spent = details?.second ?: 0.0
                    val remaining = budgetAmount - spent
                    val budget = budgets.find { it.categoryId == category.id }

                    BudgetItemCard(
                        categoryName = category.name,
                        budgetAmount = budgetAmount,
                        spent = spent,
                        remaining = remaining,
                        onEditClick = {
                            selectedCategory = category
                            selectedBudget = budget
                            showEditBudgetDialog = true
                        },
                        onDeleteClick = {
                            scope.launch {
                                budget?.let { repository.budgetDao.deleteBudget(it) }
                            }
                        }
                    )
                }
            }
        }
    }

    if (showAddBudgetDialog) {
        AddBudgetDialog(
            categories = categories.filter { category ->
                budgets.none { it.categoryId == category.id }
            },
            onDismiss = { showAddBudgetDialog = false },
            onSave = { category, amount ->
                scope.launch {
                    repository.insertBudget(
                        Budget(categoryId = category.id, amount = amount)
                    )
                    showAddBudgetDialog = false
                }
            }
        )
    }

    if (showEditBudgetDialog && selectedCategory != null) {
        EditBudgetDialog(
            category = selectedCategory!!,
            currentAmount = selectedBudget?.amount ?: 0.0,
            onDismiss = {
                showEditBudgetDialog = false
                selectedCategory = null
                selectedBudget = null
            },
            onSave = { amount ->
                scope.launch {
                    if (selectedBudget != null) {
                        repository.updateBudget(selectedBudget!!.copy(amount = amount))
                    } else {
                        repository.insertBudget(
                            Budget(categoryId = selectedCategory!!.id, amount = amount)
                        )
                    }
                    showEditBudgetDialog = false
                    selectedCategory = null
                    selectedBudget = null
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetItemCard(
    categoryName: String,
    budgetAmount: Double,
    spent: Double,
    remaining: Double,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = categoryName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Box {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.MoreVert, "Options")
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Edit") },
                            onClick = {
                                showMenu = false
                                onEditClick()
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Edit, "Edit")
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Delete") },
                            onClick = {
                                showMenu = false
                                onDeleteClick()
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Delete, "Delete")
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (budgetAmount > 0) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Budget", style = MaterialTheme.typography.bodySmall)
                        Text(
                            "LKR ${String.format("%.2f", budgetAmount)}",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Spent", style = MaterialTheme.typography.bodySmall)
                        Text(
                            "LKR ${String.format("%.2f", spent)}",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Remaining: LKR ${String.format("%.2f", remaining)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (remaining < 0) MaterialTheme.colorScheme.error
                    else MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )

                val percentage = if (budgetAmount > 0) (spent / budgetAmount * 100).toInt() else 0
                LinearProgressIndicator(
                    progress = (percentage / 100f).coerceIn(0f, 1f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    color = when {
                        percentage >= 100 -> MaterialTheme.colorScheme.error
                        percentage >= 80 -> MaterialTheme.colorScheme.tertiary
                        else -> MaterialTheme.colorScheme.primary
                    }
                )
            } else {
                Text(
                    text = "No budget set",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}

@Composable
fun AddBudgetDialog(
    categories: List<Category>,
    onDismiss: () -> Unit,
    onSave: (Category, Double) -> Unit
) {
    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    var amount by remember { mutableStateOf("") }
    var showCategoryPicker by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Budget") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(
                    onClick = { showCategoryPicker = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(selectedCategory?.name ?: "Select Category")
                }

                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Budget Amount (LKR)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val amountValue = amount.toDoubleOrNull()
                    if (selectedCategory != null && amountValue != null && amountValue > 0) {
                        onSave(selectedCategory!!, amountValue)
                    }
                },
                enabled = selectedCategory != null && amount.toDoubleOrNull() != null
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )

    if (showCategoryPicker) {
        CategoryPickerDialog(
            categories = categories,
            onDismiss = { showCategoryPicker = false },
            onCategorySelected = { category ->
                selectedCategory = category
                showCategoryPicker = false
            }
        )
    }
}

@Composable
fun EditBudgetDialog(
    category: Category,
    currentAmount: Double,
    onDismiss: () -> Unit,
    onSave: (Double) -> Unit
) {
    var amount by remember { mutableStateOf(if (currentAmount > 0) currentAmount.toString() else "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Budget: ${category.name}") },
        text = {
            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Budget Amount (LKR)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val amountValue = amount.toDoubleOrNull()
                    if (amountValue != null && amountValue > 0) {
                        onSave(amountValue)
                    }
                },
                enabled = amount.toDoubleOrNull() != null
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
