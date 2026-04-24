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
import com.expense.tracker.data.entity.Category
import com.expense.tracker.data.entity.Transaction
import com.expense.tracker.data.entity.TransactionSource
import com.expense.tracker.data.entity.TransactionType
import com.expense.tracker.data.repository.ExpenseRepository
import kotlinx.coroutines.launch
import java.time.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(
    repository: ExpenseRepository,
    onBackClick: () -> Unit,
    onTransactionAdded: () -> Unit
) {
    val categories by repository.getMainCategories().collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()

    var amount by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    var description by remember { mutableStateOf("") }
    var showCategoryPicker by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Transaction") },
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Amount (LKR)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedButton(
                onClick = { showCategoryPicker = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(selectedCategory?.name ?: "Select Category")
            }

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description (Optional)") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3
            )

            errorMessage?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Button(
                onClick = {
                    val amountValue = amount.toDoubleOrNull()
                    if (amountValue == null || amountValue <= 0) {
                        errorMessage = "Please enter a valid amount"
                    } else if (selectedCategory == null) {
                        errorMessage = "Please select a category"
                    } else {
                        scope.launch {
                            val transaction = Transaction(
                                amount = amountValue,
                                categoryId = selectedCategory!!.id,
                                timestamp = LocalDateTime.now(),
                                type = TransactionType.MANUAL,
                                description = description.ifBlank { null },
                                isCategorized = true,
                                source = TransactionSource.MANUAL
                            )
                            repository.insertTransaction(transaction)
                            onTransactionAdded()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Add Transaction")
            }
        }
    }

    if (showCategoryPicker) {
        CategoryPickerDialog(
            categories = categories,
            onDismiss = { showCategoryPicker = false },
            onCategorySelected = { category ->
                selectedCategory = category
                showCategoryPicker = false
                errorMessage = null
            }
        )
    }
}

@Composable
fun CategoryPickerDialog(
    categories: List<Category>,
    onDismiss: () -> Unit,
    onCategorySelected: (Category) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Category") },
        text = {
            LazyColumn {
                items(categories) { category ->
                    TextButton(
                        onClick = { onCategorySelected(category) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = category.name,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
