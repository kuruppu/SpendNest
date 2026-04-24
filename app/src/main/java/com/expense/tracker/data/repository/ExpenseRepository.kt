package com.expense.tracker.data.repository

import com.expense.tracker.data.dao.*
import com.expense.tracker.data.entity.*
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

class ExpenseRepository(
    val categoryDao: CategoryDao,
    val transactionDao: TransactionDao,
    val budgetDao: BudgetDao,
    val preferencesDao: UserPreferencesDao
) {
    // Categories
    fun getMainCategories(): Flow<List<Category>> = categoryDao.getMainCategories()
    fun getSubCategories(parentId: Long): Flow<List<Category>> = categoryDao.getSubCategories(parentId)
    suspend fun getCategoryById(id: Long): Category? = categoryDao.getCategoryById(id)
    suspend fun getCategoryByName(name: String): Category? = categoryDao.getMainCategoryByName(name)
    suspend fun insertCategory(category: Category): Long = categoryDao.insertCategory(category)

    // Transactions
    fun getAllTransactions(): Flow<List<Transaction>> = transactionDao.getAllTransactions()

    fun getTransactionsInRange(startDate: LocalDateTime, endDate: LocalDateTime): Flow<List<Transaction>> =
        transactionDao.getTransactionsInRange(startDate, endDate)

    fun getTransactionsByCategory(categoryId: Long, startDate: LocalDateTime, endDate: LocalDateTime): Flow<List<Transaction>> =
        transactionDao.getTransactionsByCategory(categoryId, startDate, endDate)

    fun getUncategorizedTransactions(): Flow<List<Transaction>> = transactionDao.getUncategorizedTransactions()

    suspend fun getTotalSpentForCategory(categoryId: Long, startDate: LocalDateTime, endDate: LocalDateTime): Double =
        transactionDao.getTotalSpentForCategory(categoryId, startDate, endDate) ?: 0.0

    suspend fun getTotalSpentInRange(startDate: LocalDateTime, endDate: LocalDateTime): Double =
        transactionDao.getTotalSpentInRange(startDate, endDate) ?: 0.0

    suspend fun insertTransaction(transaction: Transaction): Long = transactionDao.insertTransaction(transaction)
    suspend fun updateTransaction(transaction: Transaction) = transactionDao.updateTransaction(transaction)
    suspend fun getTransactionById(id: Long): Transaction? = transactionDao.getTransactionById(id)

    // Budgets
    fun getAllBudgets(): Flow<List<Budget>> = budgetDao.getAllBudgets()
    suspend fun getBudgetForCategory(categoryId: Long): Budget? = budgetDao.getBudgetForCategory(categoryId)
    fun getBudgetForCategoryFlow(categoryId: Long): Flow<Budget?> = budgetDao.getBudgetForCategoryFlow(categoryId)
    suspend fun insertBudget(budget: Budget): Long = budgetDao.insertBudget(budget)
    suspend fun updateBudget(budget: Budget) = budgetDao.updateBudget(budget)

    // User Preferences
    fun getUserPreferences(): Flow<UserPreferences?> = preferencesDao.getUserPreferences()
    suspend fun getUserPreferencesSync(): UserPreferences? = preferencesDao.getUserPreferencesSync()
    suspend fun updatePreferences(preferences: UserPreferences) = preferencesDao.updatePreferences(preferences)
}
