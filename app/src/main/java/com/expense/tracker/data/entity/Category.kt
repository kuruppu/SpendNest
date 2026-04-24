package com.expense.tracker.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val parentCategoryId: Long? = null, // null for main categories
    val isActive: Boolean = true
)

object CategoryDefaults {
    val mainCategories = listOf(
        "Food",
        "Transport",
        "Kids",
        "Bills",
        "Savings",
        "Other",
        "Cash Expense"
    )

    val subCategories = mapOf(
        "Savings" to listOf("Kids Saving", "Own Saving"),
        "Bills" to listOf("Credit Card", "Electricity", "Mobile", "Internet")
    )
}
