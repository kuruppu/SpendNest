package com.expense.tracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.expense.tracker.data.entity.Transaction
import com.expense.tracker.data.repository.ExpenseRepository
import com.expense.tracker.domain.model.SubcategorySpending
import com.expense.tracker.utils.BillingCycleCalculator
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.first
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDetailScreen(
    categoryId: Long,
    repository: ExpenseRepository,
    onBackClick: () -> Unit
) {
    val category by produceState<com.expense.tracker.data.entity.Category?>(initialValue = null) {
        value = repository.getCategoryById(categoryId)
    }

    val preferences by repository.getUserPreferences().collectAsState(initial = null)
    val subcategories by repository.getSubCategories(categoryId).collectAsState(initial = emptyList())

    var transactions by remember { mutableStateOf<List<Transaction>>(emptyList()) }
    var subcategorySpending by remember { mutableStateOf<List<SubcategorySpending>>(emptyList()) }

    LaunchedEffect(categoryId, preferences) {
        if (preferences != null) {
            val cycleStartDay = preferences!!.billingCycleStartDay
            val (startDate, endDate) = BillingCycleCalculator.getCurrentCycleDates(cycleStartDay)

            repository.getTransactionsByCategory(categoryId, startDate, endDate).collect {
                transactions = it
            }

            if (subcategories.isNotEmpty()) {
                val result = subcategories.map { subcat ->
                    async {
                        val spent = repository.getTotalSpentForCategory(subcat.id, startDate, endDate)
                        SubcategorySpending(
                            subcategoryId = subcat.id,
                            subcategoryName = subcat.name,
                            amount = spent,
                            percentage = 0 // Will calculate after getting totals
                        )
                    }
                }.awaitAll()

                val total = result.sumOf { it.amount }
                subcategorySpending = result.map {
                    it.copy(percentage = if (total > 0) ((it.amount / total) * 100).toInt() else 0)
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(category?.name ?: "Category Details") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (subcategorySpending.isNotEmpty() && preferences?.showSubcategoryBreakdown == true) {
                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Subcategory Breakdown",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )

                            subcategorySpending.forEach { subcat ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = subcat.subcategoryName)
                                    Text(
                                        text = "${subcat.percentage}% (LKR ${String.format("%.2f", subcat.amount)})",
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }

                                LinearProgressIndicator(
                                    progress = subcat.percentage / 100f,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Transactions",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            if (transactions.isEmpty()) {
                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp)
                        ) {
                            Text(
                                text = "No transactions in this cycle",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            } else {
                items(transactions) { transaction ->
                    TransactionItem(transaction)
                }
            }
        }
    }
}

@Composable
fun TransactionItem(transaction: Transaction) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = transaction.timestamp.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = transaction.timestamp.format(DateTimeFormatter.ofPattern("hh:mm a")),
                    style = MaterialTheme.typography.bodySmall
                )
                transaction.description?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Text(
                text = "LKR ${String.format("%.2f", transaction.amount)}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
