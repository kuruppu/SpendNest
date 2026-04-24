package com.expense.tracker.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.expense.tracker.data.repository.ExpenseRepository
import com.expense.tracker.ui.screens.*

sealed class Screen(val route: String) {
    object Dashboard : Screen("dashboard")
    object CategoryDetail : Screen("category/{categoryId}") {
        fun createRoute(categoryId: Long) = "category/$categoryId"
    }
    object TransactionHistory : Screen("history")
    object Settings : Screen("settings")
    object AddTransaction : Screen("add_transaction")
    object BudgetSetup : Screen("budget_setup")
    object BudgetManagement : Screen("budget_management")
}

@Composable
fun AppNavigation(
    navController: NavHostController,
    repository: ExpenseRepository
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Dashboard.route
    ) {
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                repository = repository,
                onCategoryClick = { categoryId ->
                    navController.navigate(Screen.CategoryDetail.createRoute(categoryId))
                },
                onHistoryClick = {
                    navController.navigate(Screen.TransactionHistory.route)
                },
                onSettingsClick = {
                    navController.navigate(Screen.Settings.route)
                },
                onAddTransactionClick = {
                    navController.navigate(Screen.AddTransaction.route)
                },
                onBudgetSetupClick = {
                    navController.navigate(Screen.BudgetSetup.route)
                },
                onBudgetManagementClick = {
                    navController.navigate(Screen.BudgetManagement.route)
                }
            )
        }

        composable(
            route = Screen.CategoryDetail.route,
            arguments = listOf(navArgument("categoryId") { type = NavType.LongType })
        ) { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getLong("categoryId") ?: return@composable
            CategoryDetailScreen(
                categoryId = categoryId,
                repository = repository,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.TransactionHistory.route) {
            TransactionHistoryScreen(
                repository = repository,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                repository = repository,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.AddTransaction.route) {
            AddTransactionScreen(
                repository = repository,
                onBackClick = { navController.popBackStack() },
                onTransactionAdded = { navController.popBackStack() }
            )
        }

        composable(Screen.BudgetSetup.route) {
            BudgetSetupScreen(
                repository = repository,
                onBackClick = { navController.popBackStack() },
                onComplete = { navController.popBackStack() }
            )
        }

        composable(Screen.BudgetManagement.route) {
            BudgetManagementScreen(
                repository = repository,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
