package com.expense.tracker.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_preferences")
data class UserPreferences(
    @PrimaryKey
    val id: Int = 1, // Single row
    val billingCycleStartDay: Int = 1, // 1-31
    val showPieCharts: Boolean = true,
    val showSubcategoryBreakdown: Boolean = true,
    val dailyReminderEnabled: Boolean = true,
    val budgetAlertThreshold: Int = 80 // Percentage
)
