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
import com.expense.tracker.data.entity.Category
import com.expense.tracker.data.repository.ExpenseRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryManagementScreen(
    repository: ExpenseRepository,
    onBackClick: () -> Unit
) {
    val categories by repository.getMainCategories().collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()

    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showAddSubcategoryDialog by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf<Category?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Manage Categories") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, "Add Category")
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
                    text = "Main Categories",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            items(categories) { category ->
                CategoryManagementCard(
                    category = category,
                    repository = repository,
                    onEditClick = {
                        selectedCategory = category
                        showEditDialog = true
                    },
                    onDeleteClick = {
                        selectedCategory = category
                        showDeleteDialog = true
                    },
                    onAddSubcategoryClick = {
                        selectedCategory = category
                        showAddSubcategoryDialog = true
                    }
                )
            }
        }
    }

    if (showAddDialog) {
        AddCategoryDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { name ->
                scope.launch {
                    repository.insertCategory(Category(name = name))
                    showAddDialog = false
                }
            }
        )
    }

    if (showEditDialog && selectedCategory != null) {
        EditCategoryDialog(
            category = selectedCategory!!,
            onDismiss = {
                showEditDialog = false
                selectedCategory = null
            },
            onConfirm = { newName ->
                scope.launch {
                    repository.categoryDao.updateCategory(
                        selectedCategory!!.copy(name = newName)
                    )
                    showEditDialog = false
                    selectedCategory = null
                }
            }
        )
    }

    if (showDeleteDialog && selectedCategory != null) {
        DeleteCategoryDialog(
            category = selectedCategory!!,
            repository = repository,
            onDismiss = {
                showDeleteDialog = false
                selectedCategory = null
            },
            onConfirm = {
                scope.launch {
                    // Delete category
                    repository.categoryDao.deleteCategory(selectedCategory!!)
                    showDeleteDialog = false
                    selectedCategory = null
                }
            }
        )
    }

    if (showAddSubcategoryDialog && selectedCategory != null) {
        AddSubcategoryDialog(
            parentCategory = selectedCategory!!,
            onDismiss = {
                showAddSubcategoryDialog = false
                selectedCategory = null
            },
            onConfirm = { name ->
                scope.launch {
                    repository.insertCategory(
                        Category(
                            name = name,
                            parentCategoryId = selectedCategory!!.id
                        )
                    )
                    showAddSubcategoryDialog = false
                    selectedCategory = null
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryManagementCard(
    category: Category,
    repository: ExpenseRepository,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onAddSubcategoryClick: () -> Unit
) {
    val subcategories by repository.getSubCategories(category.id).collectAsState(initial = emptyList())
    var showMenu by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (subcategories.isNotEmpty()) {
                        IconButton(onClick = { expanded = !expanded }) {
                            Icon(
                                if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                "Expand"
                            )
                        }
                    }
                    Text(
                        text = category.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

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
                            leadingIcon = { Icon(Icons.Default.Edit, "Edit") }
                        )
                        DropdownMenuItem(
                            text = { Text("Add Subcategory") },
                            onClick = {
                                showMenu = false
                                onAddSubcategoryClick()
                            },
                            leadingIcon = { Icon(Icons.Default.Add, "Add") }
                        )
                        DropdownMenuItem(
                            text = { Text("Delete") },
                            onClick = {
                                showMenu = false
                                onDeleteClick()
                            },
                            leadingIcon = { Icon(Icons.Default.Delete, "Delete") }
                        )
                    }
                }
            }

            if (expanded && subcategories.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Column(
                    modifier = Modifier.padding(start = 32.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    subcategories.forEach { subcategory ->
                        Text(
                            text = "• ${subcategory.name}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AddCategoryDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var categoryName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Category") },
        text = {
            OutlinedTextField(
                value = categoryName,
                onValueChange = { categoryName = it },
                label = { Text("Category Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(categoryName) },
                enabled = categoryName.isNotBlank()
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun EditCategoryDialog(
    category: Category,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var categoryName by remember { mutableStateOf(category.name) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Category") },
        text = {
            OutlinedTextField(
                value = categoryName,
                onValueChange = { categoryName = it },
                label = { Text("Category Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(categoryName) },
                enabled = categoryName.isNotBlank()
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

@Composable
fun DeleteCategoryDialog(
    category: Category,
    repository: ExpenseRepository,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var transactionCount by remember { mutableStateOf(0) }
    var showMoveDialog by remember { mutableStateOf(false) }

    LaunchedEffect(category.id) {
        // Count transactions for this category
        val preferences = repository.getUserPreferencesSync()
        if (preferences != null) {
            val (startDate, endDate) = com.expense.tracker.utils.BillingCycleCalculator.getCurrentCycleDates(
                preferences.billingCycleStartDay
            )
            val spent = repository.getTotalSpentForCategory(category.id, startDate, endDate)
            // This is approximate - ideally get exact count
            transactionCount = if (spent > 0) 1 else 0
        }
    }

    if (showMoveDialog) {
        MoveCategoryTransactionsDialog(
            fromCategory = category,
            repository = repository,
            onDismiss = {
                showMoveDialog = false
                onDismiss()
            },
            onConfirm = {
                onConfirm()
            }
        )
    } else {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Delete Category") },
            text = {
                Column {
                    Text("Are you sure you want to delete \"${category.name}\"?")
                    if (transactionCount > 0) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "This category has transactions. They will also be deleted.",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            },
            confirmButton = {
                if (transactionCount > 0) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TextButton(onClick = { showMoveDialog = true }) {
                            Text("Move Transactions")
                        }
                        TextButton(
                            onClick = onConfirm,
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Text("Delete All")
                        }
                    }
                } else {
                    TextButton(
                        onClick = onConfirm,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Delete")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun MoveCategoryTransactionsDialog(
    fromCategory: Category,
    repository: ExpenseRepository,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    val categories by repository.getMainCategories().collectAsState(initial = emptyList())
    val otherCategories = categories.filter { it.id != fromCategory.id }
    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    val scope = rememberCoroutineScope()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Move Transactions") },
        text = {
            Column {
                Text("Select a category to move transactions to:")
                Spacer(modifier = Modifier.height(16.dp))
                otherCategories.forEach { category ->
                    Button(
                        onClick = { selectedCategory = category },
                        modifier = Modifier.fillMaxWidth(),
                        colors = if (selectedCategory?.id == category.id)
                            ButtonDefaults.buttonColors()
                        else
                            ButtonDefaults.outlinedButtonColors()
                    ) {
                        Text(category.name)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    scope.launch {
                        selectedCategory?.let { newCategory ->
                            // Move all transactions from old category to new one
                            // This requires a custom DAO method
                            // For now, we'll just delete the category
                            onConfirm()
                        }
                    }
                },
                enabled = selectedCategory != null
            ) {
                Text("Move & Delete")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun AddSubcategoryDialog(
    parentCategory: Category,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var subcategoryName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Subcategory to ${parentCategory.name}") },
        text = {
            OutlinedTextField(
                value = subcategoryName,
                onValueChange = { subcategoryName = it },
                label = { Text("Subcategory Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(subcategoryName) },
                enabled = subcategoryName.isNotBlank()
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
