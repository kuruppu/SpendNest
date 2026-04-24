package com.expense.tracker

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import com.expense.tracker.data.database.ExpenseDatabase
import com.expense.tracker.data.repository.ExpenseRepository
import com.expense.tracker.ui.navigation.AppNavigation
import com.expense.tracker.ui.theme.ExpenseTrackerTheme
import com.expense.tracker.utils.NotificationHelper
import com.expense.tracker.worker.DailyReminderWorker

class MainActivity : ComponentActivity() {
    private lateinit var repository: ExpenseRepository

    private val smsPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val smsGranted = permissions[Manifest.permission.RECEIVE_SMS] ?: false
        val readSmsGranted = permissions[Manifest.permission.READ_SMS] ?: false

        if (smsGranted && readSmsGranted) {
            Toast.makeText(this, "SMS permissions granted", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(
                this,
                "SMS permissions denied. Auto-detection won't work.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            Toast.makeText(this, "Notification permission granted", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Notification permission denied", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize database and repository
        val database = ExpenseDatabase.getDatabase(applicationContext)
        repository = ExpenseRepository(
            database.categoryDao(),
            database.transactionDao(),
            database.budgetDao(),
            database.userPreferencesDao()
        )

        // Create notification channels
        NotificationHelper.createNotificationChannels(this)

        // Schedule daily reminder
        DailyReminderWorker.scheduleDailyReminder(this)

        // Request permissions
        requestPermissions()

        setContent {
            ExpenseTrackerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    AppNavigation(
                        navController = navController,
                        repository = repository
                    )
                }
            }
        }
    }

    private fun requestPermissions() {
        val smsPermissionsNeeded = mutableListOf<String>()

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECEIVE_SMS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            smsPermissionsNeeded.add(Manifest.permission.RECEIVE_SMS)
        }

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_SMS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            smsPermissionsNeeded.add(Manifest.permission.READ_SMS)
        }

        if (smsPermissionsNeeded.isNotEmpty()) {
            smsPermissionLauncher.launch(smsPermissionsNeeded.toTypedArray())
        }

        // Request notification permission for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}
