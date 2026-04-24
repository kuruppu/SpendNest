package com.expense.tracker.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.expense.tracker.MainActivity
import com.expense.tracker.data.entity.Category
import com.expense.tracker.receiver.CategoryActionReceiver

object NotificationHelper {
    private const val CHANNEL_ID_TRANSACTIONS = "expense_transactions"
    private const val CHANNEL_ID_REMINDERS = "daily_reminders"
    private const val CHANNEL_ID_BUDGET_ALERTS = "budget_alerts"

    private const val NOTIFICATION_ID_TRANSACTION = 1001
    private const val NOTIFICATION_ID_DAILY_REMINDER = 1002
    private const val NOTIFICATION_ID_BUDGET_ALERT = 1003

    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channels = listOf(
                NotificationChannel(
                    CHANNEL_ID_TRANSACTIONS,
                    "Transaction Notifications",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Notifications for new transactions detected via SMS"
                },
                NotificationChannel(
                    CHANNEL_ID_REMINDERS,
                    "Daily Reminders",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = "Daily budget reminders"
                },
                NotificationChannel(
                    CHANNEL_ID_BUDGET_ALERTS,
                    "Budget Alerts",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Alerts when approaching or exceeding budget limits"
                }
            )

            val notificationManager = context.getSystemService(NotificationManager::class.java)
            channels.forEach { notificationManager.createNotificationChannel(it) }
        }
    }

    fun showTransactionNotification(
        context: Context,
        transactionId: Long,
        amount: Double,
        isCashWithdrawal: Boolean,
        categories: List<Category>
    ) {
        val notificationId = NOTIFICATION_ID_TRANSACTION + transactionId.toInt()

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("transaction_id", transactionId)
            putExtra("categorize", true)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            transactionId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val title = if (isCashWithdrawal) {
            "Cash Withdrawal Detected"
        } else {
            "Card Payment Detected"
        }

        val message = "LKR ${String.format("%.2f", amount)} - What was this for?"

        val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID_TRANSACTIONS)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        // Add category action buttons (max 3 per notification in Android)
        // Show most common categories as quick actions
        val priorityCategories = categories.filter {
            it.name in listOf("Food", "Transport", "Bills", "Kids", "Savings", "Other")
        }.take(3)

        priorityCategories.forEach { category ->
            val categoryIntent = Intent(context, CategoryActionReceiver::class.java).apply {
                putExtra("transaction_id", transactionId)
                putExtra("category_id", category.id)
                putExtra("notification_id", notificationId)
            }

            val categoryPendingIntent = PendingIntent.getBroadcast(
                context,
                (transactionId.toInt() * 1000 + category.id.toInt()),
                categoryIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            notificationBuilder.addAction(
                0, // No icon
                category.name,
                categoryPendingIntent
            )
        }

        if (NotificationManagerCompat.from(context).areNotificationsEnabled()) {
            NotificationManagerCompat.from(context).notify(
                notificationId,
                notificationBuilder.build()
            )
        }
    }

    fun showDailyReminder(context: Context, remainingAmount: Double, cycleDescription: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val message = "You have LKR ${String.format("%.2f", remainingAmount)} remaining this cycle\n$cycleDescription"

        val notification = NotificationCompat.Builder(context, CHANNEL_ID_REMINDERS)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Daily Budget Reminder")
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        if (NotificationManagerCompat.from(context).areNotificationsEnabled()) {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_DAILY_REMINDER, notification)
        }
    }

    fun showBudgetAlert(
        context: Context,
        categoryName: String,
        percentageUsed: Int,
        isExceeded: Boolean
    ) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val title = if (isExceeded) "Budget Exceeded!" else "Budget Alert"
        val message = if (isExceeded) {
            "$categoryName budget exceeded. Reduce spending."
        } else {
            "$categoryName budget at $percentageUsed%. You are nearing your budget limit."
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID_BUDGET_ALERTS)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        if (NotificationManagerCompat.from(context).areNotificationsEnabled()) {
            NotificationManagerCompat.from(context).notify(
                NOTIFICATION_ID_BUDGET_ALERT + categoryName.hashCode(),
                notification
            )
        }
    }

    fun showCashExpenseReminder(context: Context, transactionId: Long, amount: Double) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("transaction_id", transactionId)
            putExtra("categorize", true)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            transactionId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val message = "You withdrew LKR ${String.format("%.2f", amount)}. Categorize your spending."

        val notification = NotificationCompat.Builder(context, CHANNEL_ID_REMINDERS)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Cash Expense Reminder")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        if (NotificationManagerCompat.from(context).areNotificationsEnabled()) {
            NotificationManagerCompat.from(context).notify(
                NOTIFICATION_ID_TRANSACTION + transactionId.toInt() + 1000,
                notification
            )
        }
    }
}
