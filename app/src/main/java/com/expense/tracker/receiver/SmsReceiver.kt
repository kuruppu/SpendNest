package com.expense.tracker.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.util.Log
import com.expense.tracker.data.database.ExpenseDatabase
import com.expense.tracker.data.entity.Transaction
import com.expense.tracker.data.entity.TransactionSource
import com.expense.tracker.data.entity.TransactionType
import com.expense.tracker.data.repository.ExpenseRepository
import com.expense.tracker.utils.NotificationHelper
import com.expense.tracker.utils.SmsParser
import com.expense.tracker.worker.CashExpenseReminderWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class SmsReceiver : BroadcastReceiver() {
    private val TAG = "SmsReceiver"

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
            val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)

            messages.forEach { smsMessage ->
                val sender = smsMessage.displayOriginatingAddress
                val messageBody = smsMessage.messageBody

                Log.d(TAG, "SMS from: $sender - Message: $messageBody")

                // Check if it's a bank SMS
                if (SmsParser.isBankSms(sender, messageBody)) {
                    Log.d(TAG, "Bank SMS detected")
                    handleBankSms(context, messageBody)
                }
            }
        }
    }

    private fun handleBankSms(context: Context, message: String) {
        val parsedTransaction = SmsParser.parseTransactionSms(message)

        if (parsedTransaction != null) {
            Log.d(TAG, "Transaction parsed: $parsedTransaction")

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val database = ExpenseDatabase.getDatabase(context)
                    val repository = ExpenseRepository(
                        database.categoryDao(),
                        database.transactionDao(),
                        database.budgetDao(),
                        database.userPreferencesDao()
                    )

                    // Use "Other" as temporary placeholder for uncategorized transactions
                    // But mark as NOT categorized so user knows they need to assign
                    val tempCategory = repository.getCategoryByName("Other")

                    if (tempCategory != null) {
                        val transaction = Transaction(
                            amount = parsedTransaction.amount,
                            categoryId = tempCategory.id, // Temporary
                            timestamp = parsedTransaction.timestamp,
                            type = parsedTransaction.type,
                            availableBalance = parsedTransaction.availableBalance,
                            isCategorized = false, // Mark as uncategorized
                            source = TransactionSource.SMS
                        )

                        val transactionId = repository.insertTransaction(transaction)

                        // Get all main categories for notification actions
                        val categories = repository.getMainCategories().first()

                        // Show notification with category buttons
                        NotificationHelper.showTransactionNotification(
                            context = context,
                            transactionId = transactionId,
                            amount = parsedTransaction.amount,
                            isCashWithdrawal = parsedTransaction.type == TransactionType.CASH_WITHDRAWAL,
                            categories = categories
                        )

                        // Schedule reminder for cash withdrawals
                        if (parsedTransaction.type == TransactionType.CASH_WITHDRAWAL) {
                            CashExpenseReminderWorker.scheduleReminder(
                                context = context,
                                transactionId = transactionId,
                                amount = parsedTransaction.amount
                            )
                        }

                        Log.d(TAG, "Transaction saved with ID: $transactionId")
                    } else {
                        Log.e(TAG, "Category not found: $categoryName")
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error saving transaction", e)
                }
            }
        } else {
            Log.d(TAG, "Could not parse transaction from message")
        }
    }
}
