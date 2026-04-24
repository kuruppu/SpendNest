package com.expense.tracker.domain.model

data class CategoryWithBudget(
    val categoryId: Long,
    val categoryName: String,
    val budget: Double,
    val spent: Double,
    val remaining: Double,
    val percentageUsed: Int,
    val hasSubcategories: Boolean = false
) {
    val isOverBudget: Boolean
        get() = spent > budget

    val isNearingLimit: Boolean
        get() = percentageUsed >= 80 && !isOverBudget
}

data class SubcategorySpending(
    val subcategoryId: Long,
    val subcategoryName: String,
    val amount: Double,
    val percentage: Int
)
