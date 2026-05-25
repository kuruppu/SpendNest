package com.expense.tracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.expense.tracker.data.entity.Category
import com.expense.tracker.data.entity.Transaction
import com.expense.tracker.data.entity.TransactionSource
import com.expense.tracker.data.entity.TransactionType
import com.expense.tracker.data.repository.ExpenseRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter

data class SplitItem(
    val categoryId: Long = 0,
    val categoryName: String = "",
    val amount: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SplitTransactionScreen(
    transactionId: Long,
    repository: ExpenseRepository,
    onNavigateBack: () -> Unit
) {
    var transaction by remember { mutableStateOf<Transaction?>(null) }
    var categories by remember { mutableStateOf<List<Category>>(emptyList()) }
    var splitItems by remember { mutableStateOf(listOf(SplitItem())) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(transactionId) {
        transaction = repository.getTransactionById(transactionId)
        categories = repository.getMainCategories().first()
    }

    val totalAmount = transaction?.amount ?: 0.0
    val allocatedAmount = splitItems.sumOf { it.amount.toDoubleOrNull() ?: 0.0 }
    val remainingAmount = totalAmount - allocatedAmount
    val isValid = remainingAmount == 0.0 && splitItems.all { it.categoryId != 0L && it.amount.isNotEmpty() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Split Transaction") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (transaction == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Transaction Info Card
                item {
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
                                text = "Cash Withdrawal",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "LKR ${String.format("%.2f", totalAmount)}",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = transaction!!.timestamp.format(DateTimeFormatter.ofPattern("MMM dd, yyyy hh:mm a")),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                            )
                        }
                    }
                }

                // Allocation Summary
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = if (remainingAmount == 0.0 && allocatedAmount > 0)
                                MaterialTheme.colorScheme.tertiaryContainer
                            else if (remainingAmount < 0)
                                MaterialTheme.colorScheme.errorContainer
                            else
                                MaterialTheme.colorScheme.secondaryContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Total Amount:", style = MaterialTheme.typography.bodyMedium)
                                Text(
                                    "LKR ${String.format("%.2f", totalAmount)}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Allocated:", style = MaterialTheme.typography.bodyMedium)
                                Text(
                                    "LKR ${String.format("%.2f", allocatedAmount)}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Divider()
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    "Remaining:",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    "LKR ${String.format("%.2f", remainingAmount)}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = when {
                                        remainingAmount == 0.0 && allocatedAmount > 0 -> MaterialTheme.colorScheme.tertiary
                                        remainingAmount < 0 -> MaterialTheme.colorScheme.error
                                        else -> MaterialTheme.colorScheme.onSecondaryContainer
                                    }
                                )
                            }
                        }
                    }
                }

                // Split Items Title
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Split Allocation",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        TextButton(
                            onClick = {
                                splitItems = splitItems + SplitItem()
                            }
                        ) {
                            Icon(Icons.Default.Add, "Add", modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Add Split")
                        }
                    }
                }

                // Split Items
                itemsIndexed(splitItems) { index, item ->
                    SplitItemCard(
                        item = item,
                        categories = categories,
                        onCategorySelected = { categoryId, categoryName ->
                            splitItems = splitItems.toMutableList().apply {
                                this[index] = item.copy(categoryId = categoryId, categoryName = categoryName)
                            }
                        },
                        onAmountChanged = { amount ->
                            splitItems = splitItems.toMutableList().apply {
                                this[index] = item.copy(amount = amount)
                            }
                        },
                        onRemove = if (splitItems.size > 1) {
                            {
                                splitItems = splitItems.toMutableList().apply { removeAt(index) }
                            }
                        } else null
                    )
                }

                // Error Message
                if (errorMessage != null) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            )
                        ) {
                            Text(
                                text = errorMessage!!,
                                modifier = Modifier.padding(16.dp),
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }

                // Save Button
                item {
                    Button(
                        onClick = {
                            scope.launch {
                                isLoading = true
                                errorMessage = null
                                try {
                                    // Delete existing split transactions if any
                                    repository.deleteSplitTransactions(transactionId)

                                    // Mark original transaction as split and ignored (parent transaction)
                                    val updatedParent = transaction!!.copy(
                                        isSplit = true,
                                        isIgnored = true,
                                        isCategorized = true
                                    )
                                    repository.updateTransaction(updatedParent)

                                    // Create split transactions
                                    splitItems.forEach { splitItem ->
                                        val splitTransaction = Transaction(
                                            amount = splitItem.amount.toDouble(),
                                            categoryId = splitItem.categoryId,
                                            timestamp = transaction!!.timestamp,
                                            type = TransactionType.CASH_WITHDRAWAL,
                                            availableBalance = transaction!!.availableBalance,
                                            isCategorized = true,
                                            source = TransactionSource.SMS,
                                            parentTransactionId = transactionId,
                                            isSplit = true
                                        )
                                        repository.insertTransaction(splitTransaction)
                                    }

                                    onNavigateBack()
                                } catch (e: Exception) {
                                    errorMessage = "Error saving split: ${e.message}"
                                    isLoading = false
                                }
                            }
                        },
                        enabled = isValid && !isLoading,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Text("Save Split")
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SplitItemCard(
    item: SplitItem,
    categories: List<Category>,
    onCategorySelected: (Long, String) -> Unit,
    onAmountChanged: (String) -> Unit,
    onRemove: (() -> Unit)?
) {
    var expandedDropdown by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Category Dropdown
            ExposedDropdownMenuBox(
                expanded = expandedDropdown,
                onExpandedChange = { expandedDropdown = it },
                modifier = Modifier.weight(1f)
            ) {
                OutlinedTextField(
                    value = item.categoryName.ifEmpty { "Select Category" },
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDropdown) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors()
                )

                ExposedDropdownMenu(
                    expanded = expandedDropdown,
                    onDismissRequest = { expandedDropdown = false }
                ) {
                    categories.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(category.name) },
                            onClick = {
                                onCategorySelected(category.id, category.name)
                                expandedDropdown = false
                            }
                        )
                    }
                }
            }

            // Amount Input
            OutlinedTextField(
                value = item.amount,
                onValueChange = onAmountChanged,
                label = { Text("Amount") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.weight(0.7f),
                singleLine = true
            )

            // Remove Button
            if (onRemove != null) {
                IconButton(onClick = onRemove) {
                    Icon(
                        Icons.Default.Delete,
                        "Remove",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            } else {
                // Placeholder to maintain spacing
                Spacer(modifier = Modifier.width(48.dp))
            }
        }
    }
}
