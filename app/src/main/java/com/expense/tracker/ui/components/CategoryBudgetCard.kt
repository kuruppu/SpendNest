package com.expense.tracker.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.expense.tracker.domain.model.CategoryWithBudget

@Composable
fun CategoryBudgetCard(
    categoryBudget: CategoryWithBudget,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = categoryBudget.categoryName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "${categoryBudget.percentageUsed}%",
                    style = MaterialTheme.typography.bodyLarge,
                    color = when {
                        categoryBudget.isOverBudget -> MaterialTheme.colorScheme.error
                        categoryBudget.isNearingLimit -> MaterialTheme.colorScheme.tertiary
                        else -> MaterialTheme.colorScheme.primary
                    }
                )
            }

            // Progress bar
            LinearProgressIndicator(
                progress = (categoryBudget.percentageUsed / 100f).coerceIn(0f, 1f),
                modifier = Modifier.fillMaxWidth(),
                color = when {
                    categoryBudget.isOverBudget -> MaterialTheme.colorScheme.error
                    categoryBudget.isNearingLimit -> MaterialTheme.colorScheme.tertiary
                    else -> MaterialTheme.colorScheme.primary
                }
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Spent",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "LKR ${String.format("%.2f", categoryBudget.spent)}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Budget",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "LKR ${String.format("%.2f", categoryBudget.budget)}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            if (categoryBudget.budget > 0) {
                Text(
                    text = "Remaining: LKR ${String.format("%.2f", categoryBudget.remaining)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (categoryBudget.remaining < 0)
                        MaterialTheme.colorScheme.error
                    else
                        MaterialTheme.colorScheme.onSurface
                )
            }

            if (categoryBudget.isOverBudget) {
                Text(
                    text = "Budget exceeded. Reduce spending.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            } else if (categoryBudget.isNearingLimit) {
                Text(
                    text = "You are nearing your budget limit",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.tertiary
                )
            }
        }
    }
}
