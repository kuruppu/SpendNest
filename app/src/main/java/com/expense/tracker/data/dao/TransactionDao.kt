package com.expense.tracker.data.dao

import androidx.room.*
import com.expense.tracker.data.entity.Transaction
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    fun getAllTransactions(): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE timestamp >= :startDate AND timestamp < :endDate ORDER BY timestamp DESC")
    fun getTransactionsInRange(startDate: LocalDateTime, endDate: LocalDateTime): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE categoryId = :categoryId AND timestamp >= :startDate AND timestamp < :endDate ORDER BY timestamp DESC")
    fun getTransactionsByCategory(categoryId: Long, startDate: LocalDateTime, endDate: LocalDateTime): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE isCategorized = 0 ORDER BY timestamp DESC")
    fun getUncategorizedTransactions(): Flow<List<Transaction>>

    @Query("SELECT SUM(amount) FROM transactions WHERE categoryId = :categoryId AND timestamp >= :startDate AND timestamp < :endDate")
    suspend fun getTotalSpentForCategory(categoryId: Long, startDate: LocalDateTime, endDate: LocalDateTime): Double?

    @Query("SELECT SUM(amount) FROM transactions WHERE timestamp >= :startDate AND timestamp < :endDate")
    suspend fun getTotalSpentInRange(startDate: LocalDateTime, endDate: LocalDateTime): Double?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: Transaction): Long

    @Update
    suspend fun updateTransaction(transaction: Transaction)

    @Delete
    suspend fun deleteTransaction(transaction: Transaction)

    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getTransactionById(id: Long): Transaction?
}
