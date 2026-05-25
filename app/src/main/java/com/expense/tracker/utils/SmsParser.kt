package com.expense.tracker.utils

import com.expense.tracker.data.entity.TransactionType
import java.time.LocalDateTime

data class ParsedTransaction(
    val amount: Double,
    val type: TransactionType,
    val availableBalance: Double?,
    val timestamp: LocalDateTime = LocalDateTime.now()
)

object SmsParser {
    // Keywords indicating failed/declined/cancelled transactions
    private val failureKeywords = listOf(
        "declined", "failed", "reversed", "unsuccessful",
        "cancelled", "rejected", "denied", "blocked",
        "invalid", "not authorized", "insufficient"
    )

    // Common bank SMS patterns
    private val cardPaymentPatterns = listOf(
        // Pattern: "Rs.1,234.56 spent on Card ending 5678"
        Regex("""(?:Rs\.?|LKR)\s*([\d,]+\.?\d*)\s*(?:spent|debited|used).*card""", RegexOption.IGNORE_CASE),
        // Pattern: "Card txn: Rs 1234.56"
        Regex("""card.*?(?:Rs\.?|LKR)\s*([\d,]+\.?\d*)""", RegexOption.IGNORE_CASE),
        // Pattern: "Purchase of Rs.1234.56"
        Regex("""purchase.*?(?:Rs\.?|LKR)\s*([\d,]+\.?\d*)""", RegexOption.IGNORE_CASE),
        // Pattern: "Debit Card ending 5678 for Rs.1234.56"
        Regex("""debit\s+card.*?(?:Rs\.?|LKR)\s*([\d,]+\.?\d*)""", RegexOption.IGNORE_CASE)
    )

    private val atmWithdrawalPatterns = listOf(
        // Pattern: "ATM withdrawal of Rs.5000"
        Regex("""ATM.*?(?:withdrawal|cash).*?(?:Rs\.?|LKR)\s*([\d,]+\.?\d*)""", RegexOption.IGNORE_CASE),
        // Pattern: "Cash withdrawn Rs.5000"
        Regex("""cash\s+(?:withdrawn|withdrawal).*?(?:Rs\.?|LKR)\s*([\d,]+\.?\d*)""", RegexOption.IGNORE_CASE),
        // Pattern: "Withdrawn Rs.5000 from ATM"
        Regex("""withdrawn.*?(?:Rs\.?|LKR)\s*([\d,]+\.?\d*).*?ATM""", RegexOption.IGNORE_CASE)
    )

    private val balancePattern = Regex(
        """(?:available|avbl|bal|balance).*?(?:Rs\.?|LKR)\s*([\d,]+\.?\d*)""",
        RegexOption.IGNORE_CASE
    )

    fun parseTransactionSms(message: String): ParsedTransaction? {
        // Try to parse as card payment
        cardPaymentPatterns.forEach { pattern ->
            pattern.find(message)?.let { match ->
                val amount = extractAmount(match.groupValues[1])
                val balance = extractBalance(message)
                return ParsedTransaction(
                    amount = amount,
                    type = TransactionType.CARD_PAYMENT,
                    availableBalance = balance
                )
            }
        }

        // Try to parse as ATM withdrawal
        atmWithdrawalPatterns.forEach { pattern ->
            pattern.find(message)?.let { match ->
                val amount = extractAmount(match.groupValues[1])
                val balance = extractBalance(message)
                return ParsedTransaction(
                    amount = amount,
                    type = TransactionType.CASH_WITHDRAWAL,
                    availableBalance = balance
                )
            }
        }

        return null
    }

    private fun extractAmount(amountStr: String): Double {
        return amountStr.replace(",", "").toDoubleOrNull() ?: 0.0
    }

    private fun extractBalance(message: String): Double? {
        return balancePattern.find(message)?.let { match ->
            extractAmount(match.groupValues[1])
        }
    }

    fun isBankSms(sender: String, message: String): Boolean {
        val bankKeywords = listOf(
            "bank", "card", "atm", "transaction", "debit", "credit",
            "purchase", "withdrawn", "balance", "avbl"
        )

        // Check if sender looks like a bank (usually short codes or specific patterns)
        val isBankSender = sender.length <= 6 || sender.contains("bank", ignoreCase = true)

        // Check if message contains bank-related keywords
        val hasBankKeywords = bankKeywords.any {
            message.contains(it, ignoreCase = true)
        }

        return isBankSender && hasBankKeywords
    }

    /**
     * Checks if the SMS indicates a failed/declined/reversed transaction
     */
    fun isFailedTransaction(message: String): Boolean {
        return failureKeywords.any {
            message.contains(it, ignoreCase = true)
        }
    }
}
