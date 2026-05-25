package com.expense.tracker.receiver

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.expense.tracker.MainActivity
import com.expense.tracker.data.database.ExpenseDatabase
import com.expense.tracker.data.repository.ExpenseRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CategoryActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val transactionId = intent.getLongExtra("transaction_id", -1L)
        val action = intent.getStringExtra("action") ?: "categorize"
        val notificationId = intent.getIntExtra("notification_id", -1)

        if (transactionId == -1L) return

        when (action) {
            "ignore" -> handleIgnoreAction(context, transactionId, notificationId)
            "split" -> handleSplitAction(context, transactionId, notificationId)
            "categorize" -> {
                val categoryId = intent.getLongExtra("category_id", -1L)
                if (categoryId != -1L) {
                    handleCategorizeAction(context, transactionId, categoryId, notificationId)
                }
            }
        }
    }

    private fun handleIgnoreAction(context: Context, transactionId: Long, notificationId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val database = ExpenseDatabase.getDatabase(context)
                val repository = ExpenseRepository(
                    database.categoryDao(),
                    database.transactionDao(),
                    database.budgetDao(),
                    database.userPreferencesDao()
                )

                // Get the transaction
                val transaction = repository.getTransactionById(transactionId)
                if (transaction != null) {
                    // Mark as ignored
                    val updated = transaction.copy(isIgnored = true)
                    repository.updateTransaction(updated)

                    // Dismiss the notification
                    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    notificationManager.cancel(notificationId)

                    Log.d("CategoryActionReceiver", "Transaction $transactionId ignored")
                }
            } catch (e: Exception) {
                Log.e("CategoryActionReceiver", "Error ignoring transaction", e)
            }
        }
    }

    private fun handleSplitAction(context: Context, transactionId: Long, notificationId: Int) {
        // Dismiss notification
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(notificationId)

        // Open MainActivity with split intent
        val mainIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("transaction_id", transactionId)
            putExtra("action", "split")
        }
        context.startActivity(mainIntent)
    }

    private fun handleCategorizeAction(context: Context, transactionId: Long, categoryId: Long, notificationId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val database = ExpenseDatabase.getDatabase(context)
                val repository = ExpenseRepository(
                    database.categoryDao(),
                    database.transactionDao(),
                    database.budgetDao(),
                    database.userPreferencesDao()
                )

                // Get the transaction
                val transaction = repository.getTransactionById(transactionId)
                if (transaction != null) {
                    // Update category and mark as categorized
                    val updated = transaction.copy(
                        categoryId = categoryId,
                        isCategorized = true
                    )
                    repository.updateTransaction(updated)

                    // Dismiss the notification
                    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    notificationManager.cancel(notificationId)

                    Log.d("CategoryActionReceiver", "Transaction $transactionId categorized to category $categoryId")
                }
            } catch (e: Exception) {
                Log.e("CategoryActionReceiver", "Error categorizing transaction", e)
            }
        }
    }
}
