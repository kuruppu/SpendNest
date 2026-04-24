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
import com.expense.tracker.data.repository.ExpenseRepository
import com.expense.tracker.domain.model.CategoryWithBudget
import com.expense.tracker.ui.components.CategoryBudgetCard
import com.expense.tracker.utils.BillingCycleCalculator
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    repository: ExpenseRepository,
    onCategoryClick: (Long) -> Unit,
    onHistoryClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onAddTransactionClick: () -> Unit,
    onBudgetSetupClick: () -> Unit
) {
    val preferences by repository.getUserPreferences().collectAsState(initial = null)
    val categories by repository.getMainCategories().collectAsState(initial = emptyList())
    val budgets by repository.getAllBudgets().collectAsState(initial = emptyList())

    val categoriesWithBudget = remember(categories, budgets, preferences) {
        mutableStateOf<List<CategoryWithBudget>>(emptyList())
    }

    LaunchedEffect(categories, budgets, preferences) {
        if (preferences != null && categories.isNotEmpty()) {
            val cycleStartDay = preferences!!.billingCycleStartDay
            val (startDate, endDate) = BillingCycleCalculator.getCurrentCycleDates(cycleStartDay)

            val result = categories.map { category ->
                async {
                    val budget = budgets.find { it.categoryId == category.id }
                    val spent = repository.getTotalSpentForCategory(category.id, startDate, endDate)
                    val budgetAmount = budget?.amount ?: 0.0
                    val remaining = budgetAmount - spent
                    val percentageUsed = if (budgetAmount > 0) {
                        ((spent / budgetAmount) * 100).toInt()
                    } else 0

                    val subcategories = repository.getSubCategories(category.id)
                    var hasSubcategories = false
                    subcategories.collect { list ->
                        hasSubcategories = list.isNotEmpty()
                    }

                    CategoryWithBudget(
                        categoryId = category.id,
                        categoryName = category.name,
                        budget = budgetAmount,
                        spent = spent,
                        remaining = remaining,
                        percentageUsed = percentageUsed,
                        hasSubcategories = hasSubcategories
                    )
                }
            }.awaitAll()

            categoriesWithBudget.value = result
        }
    }

    val totalBudget = categoriesWithBudget.value.sumOf { it.budget }
    val totalSpent = categoriesWithBudget.value.sumOf { it.spent }
    val totalRemaining = totalBudget - totalSpent

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Expense Tracker") },
                actions = {
                    IconButton(onClick = onHistoryClick) {
                        Icon(Icons.Default.History, "History")
                    }
                    IconButton(onClick = onSettingsClick) {
                        Icon(Icons.Default.Settings, "Settings")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddTransactionClick) {
                Icon(Icons.Default.Add, "Add Transaction")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                // Summary Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = preferences?.let {
                                BillingCycleCalculator.getCycleDescription(it.billingCycleStartDay)
                            } ?: "Current Cycle",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Divider(modifier = Modifier.padding(vertical = 8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Total Budget", style = MaterialTheme.typography.bodySmall)
                                Text(
                                    "LKR ${String.format("%.2f", totalBudget)}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Spent", style = MaterialTheme.typography.bodySmall)
                                Text(
                                    "LKR ${String.format("%.2f", totalSpent)}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Remaining", style = MaterialTheme.typography.bodySmall)
                                Text(
                                    "LKR ${String.format("%.2f", totalRemaining)}",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = if (totalRemaining < 0) MaterialTheme.colorScheme.error
                                    else MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }

            if (categoriesWithBudget.value.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = onBudgetSetupClick
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Default.AccountBalance,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp)
                            )
                            Text(
                                "Set Up Your Budgets",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "Tap here to configure your category budgets",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            } else {
                item {
                    Text(
                        text = "Categories",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                items(categoriesWithBudget.value) { categoryBudget ->
                    CategoryBudgetCard(
                        categoryBudget = categoryBudget,
                        onClick = { onCategoryClick(categoryBudget.categoryId) }
                    )
                }
            }
        }
    }
}
