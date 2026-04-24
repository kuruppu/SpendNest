package com.expense.tracker.utils

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth

object BillingCycleCalculator {
    /**
     * Calculate the start and end dates of the current billing cycle
     * based on the cycle start day
     */
    fun getCurrentCycleDates(cycleStartDay: Int): Pair<LocalDateTime, LocalDateTime> {
        val today = LocalDate.now()
        val currentMonth = YearMonth.from(today)

        // Adjust cycle start day if it exceeds the current month's length
        val adjustedStartDay = minOf(cycleStartDay, currentMonth.lengthOfMonth())

        val cycleStartDate = if (today.dayOfMonth >= adjustedStartDay) {
            // We're in the current cycle
            LocalDate.of(today.year, today.month, adjustedStartDay)
        } else {
            // We're in the previous month's cycle
            val previousMonth = currentMonth.minusMonths(1)
            val prevMonthStartDay = minOf(cycleStartDay, previousMonth.lengthOfMonth())
            LocalDate.of(previousMonth.year, previousMonth.month, prevMonthStartDay)
        }

        // Calculate end date (one month from start date, same day)
        val nextMonth = YearMonth.from(cycleStartDate).plusMonths(1)
        val nextMonthStartDay = minOf(cycleStartDay, nextMonth.lengthOfMonth())
        val cycleEndDate = LocalDate.of(nextMonth.year, nextMonth.month, nextMonthStartDay)

        return Pair(
            cycleStartDate.atStartOfDay(),
            cycleEndDate.atStartOfDay()
        )
    }

    /**
     * Get the number of days remaining in the current cycle
     */
    fun getDaysRemainingInCycle(cycleStartDay: Int): Int {
        val (_, endDate) = getCurrentCycleDates(cycleStartDay)
        val today = LocalDate.now()
        return (endDate.toLocalDate().toEpochDay() - today.toEpochDay()).toInt()
    }

    /**
     * Get a readable description of the current cycle
     */
    fun getCycleDescription(cycleStartDay: Int): String {
        val (startDate, endDate) = getCurrentCycleDates(cycleStartDay)
        val formatter = java.time.format.DateTimeFormatter.ofPattern("MMM dd")
        return "${startDate.format(formatter)} - ${endDate.format(formatter)}"
    }
}
