package com.expense.tracker.ui.screens

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.expense.tracker.data.entity.UserPreferences
import com.expense.tracker.data.repository.ExpenseRepository
import com.expense.tracker.worker.DailyReminderWorker
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    repository: ExpenseRepository,
    onBackClick: () -> Unit,
    onCategoryManagementClick: () -> Unit = {}
) {
    val preferences by repository.getUserPreferences().collectAsState(initial = null)
    val scope = rememberCoroutineScope()

    var showCycleDialog by remember { mutableStateOf(false) }

    val smsPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        // Handle permission result if needed
    }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        // Handle permission result if needed
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
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
            item {
                Text(
                    text = "Billing Cycle",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { showCycleDialog = true }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Cycle Start Day",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Day of month when your cycle begins",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        Text(
                            text = "${preferences?.billingCycleStartDay ?: 1}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            item {
                Text(
                    text = "Notifications",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Daily Reminder",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "Daily notification at 8:00 AM",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            Switch(
                                checked = preferences?.dailyReminderEnabled ?: true,
                                onCheckedChange = { enabled ->
                                    scope.launch {
                                        preferences?.let { prefs ->
                                            val updated = prefs.copy(dailyReminderEnabled = enabled)
                                            repository.updatePreferences(updated)

                                            if (enabled) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                                    notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                                }
                                                // Schedule daily reminder
                                                // DailyReminderWorker.scheduleDailyReminder(context)
                                            } else {
                                                // Cancel daily reminder
                                                // DailyReminderWorker.cancelDailyReminder(context)
                                            }
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Categories",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onCategoryManagementClick
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Manage Categories",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Add, edit, or delete categories and subcategories",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        Icon(Icons.Default.ArrowForward, "Go")
                    }
                }
            }

            item {
                Text(
                    text = "Display",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Show Pie Charts",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            Switch(
                                checked = preferences?.showPieCharts ?: true,
                                onCheckedChange = { enabled ->
                                    scope.launch {
                                        preferences?.let {
                                            repository.updatePreferences(it.copy(showPieCharts = enabled))
                                        }
                                    }
                                }
                            )
                        }

                        Divider()

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Show Subcategory Breakdown",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            Switch(
                                checked = preferences?.showSubcategoryBreakdown ?: true,
                                onCheckedChange = { enabled ->
                                    scope.launch {
                                        preferences?.let {
                                            repository.updatePreferences(it.copy(showSubcategoryBreakdown = enabled))
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Permissions",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        smsPermissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.RECEIVE_SMS,
                                Manifest.permission.READ_SMS
                            )
                        )
                    }
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "SMS Permissions",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Required to automatically detect bank transactions",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }

    if (showCycleDialog) {
        BillingCycleDialog(
            currentDay = preferences?.billingCycleStartDay ?: 1,
            onDismiss = { showCycleDialog = false },
            onConfirm = { day ->
                scope.launch {
                    preferences?.let {
                        repository.updatePreferences(it.copy(billingCycleStartDay = day))
                    }
                    showCycleDialog = false
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BillingCycleDialog(
    currentDay: Int,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    var inputValue by remember { mutableStateOf(currentDay.toString()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Cycle Start Day") },
        text = {
            Column {
                Text("Choose the day of the month when your billing cycle starts:")

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = inputValue,
                    onValueChange = { value ->
                        // Allow empty string for deletion
                        if (value.isEmpty()) {
                            inputValue = ""
                            errorMessage = null
                        } else {
                            // Only allow digits
                            val filtered = value.filter { it.isDigit() }
                            if (filtered.isNotEmpty()) {
                                val number = filtered.toIntOrNull()
                                if (number != null) {
                                    if (number in 1..31) {
                                        inputValue = filtered
                                        errorMessage = null
                                    } else if (number > 31) {
                                        // Don't update if exceeds 31
                                        errorMessage = "Must be between 1 and 31"
                                    } else {
                                        inputValue = filtered
                                        errorMessage = null
                                    }
                                }
                            }
                        }
                    },
                    label = { Text("Day (1-31)") },
                    singleLine = true,
                    isError = errorMessage != null,
                    supportingText = errorMessage?.let { { Text(it) } },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    )
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val day = inputValue.toIntOrNull()
                    if (day != null && day in 1..31) {
                        onConfirm(day)
                    }
                },
                enabled = inputValue.toIntOrNull()?.let { it in 1..31 } == true
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
