package com.expense.tracker.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.expense.tracker.data.entity.Category
import com.expense.tracker.data.entity.Transaction
import com.expense.tracker.data.entity.TransactionType
import com.expense.tracker.data.repository.ExpenseRepository
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UncategorizedTransactionsSection(
    uncategorizedTransactions: List<Transaction>,
    categories: List<Category>,
    repository: ExpenseRepository,
    modifier: Modifier = Modifier,
    onSplitTransaction: (Long) -> Unit = {}
) {
    if (uncategorizedTransactions.isEmpty()) return

    val scope = rememberCoroutineScope()
    var selectedTransaction by remember { mutableStateOf<Transaction?>(null) }
    var showCategoryDialog by remember { mutableStateOf(false) }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    Icons.Default.Warning,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
                Text(
                    text = "Uncategorized Transactions",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }

            Text(
                text = "${uncategorizedTransactions.size} transaction${if (uncategorizedTransactions.size > 1) "s" else ""} need${if (uncategorizedTransactions.size == 1) "s" else ""} category assignment",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onErrorContainer
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(uncategorizedTransactions.take(5)) { transaction ->
                    UncategorizedTransactionCard(
                        transaction = transaction,
                        onClick = {
                            selectedTransaction = transaction
                            showCategoryDialog = true
                        }
                    )
                }
            }
        }
    }

    if (showCategoryDialog && selectedTransaction != null) {
        QuickCategoryDialog(
            transaction = selectedTransaction!!,
            categories = categories,
            onDismiss = {
                showCategoryDialog = false
                selectedTransaction = null
            },
            onCategorySelected = { category ->
                scope.launch {
                    val updated = selectedTransaction!!.copy(
                        categoryId = category.id,
                        isCategorized = true
                    )
                    repository.updateTransaction(updated)
                    showCategoryDialog = false
                    selectedTransaction = null
                }
            },
            onIgnore = {
                scope.launch {
                    val updated = selectedTransaction!!.copy(isIgnored = true)
                    repository.updateTransaction(updated)
                    showCategoryDialog = false
                    selectedTransaction = null
                }
            },
            onSplit = {
                onSplitTransaction(selectedTransaction!!.id)
                showCategoryDialog = false
                selectedTransaction = null
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UncategorizedTransactionCard(
    transaction: Transaction,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.width(200.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "LKR ${String.format("%.2f", transaction.amount)}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = transaction.timestamp.format(DateTimeFormatter.ofPattern("MMM dd, hh:mm a")),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Tap to categorize",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun QuickCategoryDialog(
    transaction: Transaction,
    categories: List<Category>,
    onDismiss: () -> Unit,
    onCategorySelected: (Category) -> Unit,
    onIgnore: () -> Unit = {},
    onSplit: () -> Unit = {}
) {
    val isCashWithdrawal = transaction.type == TransactionType.CASH_WITHDRAWAL

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text("Categorize Transaction")
                Text(
                    text = "LKR ${String.format("%.2f", transaction.amount)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = if (isCashWithdrawal) "Cash Withdrawal" else "Card Payment",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            onIgnore()
                            onDismiss()
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Ignore")
                    }
                    if (isCashWithdrawal) {
                        OutlinedButton(
                            onClick = {
                                onSplit()
                                onDismiss()
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Split")
                        }
                    }
                }

                Divider(modifier = Modifier.padding(vertical = 8.dp))

                Text(
                    text = "Select Category:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )

                categories.forEach { category ->
                    Button(
                        onClick = { onCategorySelected(category) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(category.name)
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
