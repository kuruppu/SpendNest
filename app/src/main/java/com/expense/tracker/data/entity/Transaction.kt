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
    indices = [Index("categoryId"), Index("timestamp")]
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
    val source: TransactionSource = TransactionSource.MANUAL
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
