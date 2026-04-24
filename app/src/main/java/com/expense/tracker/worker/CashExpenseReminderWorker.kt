package com.expense.tracker.worker

import android.content.Context
import android.util.Log
import androidx.work.*
import com.expense.tracker.utils.NotificationHelper
import java.util.concurrent.TimeUnit

class CashExpenseReminderWorker(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        return try {
            val transactionId = inputData.getLong("transaction_id", -1L)
            val amount = inputData.getDouble("amount", 0.0)

            if (transactionId != -1L) {
                NotificationHelper.showCashExpenseReminder(
                    context = applicationContext,
                    transactionId = transactionId,
                    amount = amount
                )

                Log.d("CashExpenseReminder", "Reminder sent for transaction $transactionId")
            }

            Result.success()
        } catch (e: Exception) {
            Log.e("CashExpenseReminder", "Error sending reminder", e)
            Result.failure()
        }
    }

    companion object {
        fun scheduleReminder(context: Context, transactionId: Long, amount: Double) {
            // Schedule reminders at 4 hours, 12 hours, and 24 hours
            val delays = listOf(4L, 12L, 24L)

            delays.forEachIndexed { index, delay ->
                val inputData = Data.Builder()
                    .putLong("transaction_id", transactionId)
                    .putDouble("amount", amount)
                    .build()

                val reminderWork = OneTimeWorkRequestBuilder<CashExpenseReminderWorker>()
                    .setInitialDelay(delay, TimeUnit.HOURS)
                    .setInputData(inputData)
                    .build()

                WorkManager.getInstance(context).enqueue(reminderWork)

                Log.d("CashExpenseReminder", "Scheduled reminder #$index for transaction $transactionId in $delay hours")
            }
        }
    }
}
