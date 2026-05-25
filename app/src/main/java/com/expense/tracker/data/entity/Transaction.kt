package com.expense.tracker.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(
    tableName = "transactions",
    foreignKeys = [
        ForeignKey(
            entity = Category::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [Index("categoryId"), Index("timestamp"), Index("parentTransactionId")]
)
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val amount: Double,
    val categoryId: Long,
    val timestamp: LocalDateTime,
    val type: TransactionType,
    val description: String? = null,
    val availableBalance: Double? = null,
    val isCategorized: Boolean = false, // For cash withdrawals
    val source: TransactionSource = TransactionSource.MANUAL,
    val isIgnored: Boolean = false, // For ignored/cancelled transactions
    val parentTransactionId: Long? = null, // For split transactions (links to parent)
    val isSplit: Boolean = false // True if this is a split transaction
)

enum class TransactionType {
    CARD_PAYMENT,
    CASH_WITHDRAWAL,
    MANUAL
}

enum class TransactionSource {
    SMS,
    MANUAL
}
