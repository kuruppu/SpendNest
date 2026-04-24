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
import com.expense.tracker.data.entity.Budget
import com.expense.tracker.data.entity.Category
import com.expense.tracker.data.repository.ExpenseRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetSetupScreen(
    repository: ExpenseRepository,
    onBackClick: () -> Unit,
    onComplete: () -> Unit
) {
    val categories by repository.getMainCategories().collectAsState(initial = emptyList())
    val existingBudgets by repository.getAllBudgets().collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()

    val budgetAmounts = remember { mutableStateMapOf<Long, String>() }

    LaunchedEffect(categories, existingBudgets) {
        categories.forEach { category ->
            val existingBudget = existingBudgets.find { it.categoryId == category.id }
            budgetAmounts[category.id] = existingBudget?.amount?.toString() ?: ""
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Budget Setup") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        text = "Set budgets for each category",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                items(categories) { category ->
                    BudgetInputCard(
                        category = category,
                        amount = budgetAmounts[category.id] ?: "",
                        onAmountChange = { newAmount ->
                            budgetAmounts[category.id] = newAmount
                        }
                    )
                }
            }

            Button(
                onClick = {
                    scope.launch {
                        categories.forEach { category ->
                            val amountStr = budgetAmounts[category.id]
                            val amount = amountStr?.toDoubleOrNull() ?: 0.0

                            if (amount > 0) {
                                val existingBudget = existingBudgets.find { it.categoryId == category.id }
                                if (existingBudget != null) {
                                    repository.updateBudget(existingBudget.copy(amount = amount))
                                } else {
                                    repository.insertBudget(
                                        Budget(
                                            categoryId = category.id,
                                            amount = amount
                                        )
                                    )
                                }
                            }
                        }
                        onComplete()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text("Save Budgets")
            }
        }
    }
}

@Composable
fun BudgetInputCard(
    category: Category,
    amount: String,
    onAmountChange: (String) -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = category.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            OutlinedTextField(
                value = amount,
                onValueChange = onAmountChange,
                label = { Text("Budget Amount (LKR)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }
    }
}
