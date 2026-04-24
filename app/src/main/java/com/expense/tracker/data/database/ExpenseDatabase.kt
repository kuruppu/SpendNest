package com.expense.tracker.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.expense.tracker.data.dao.*
import com.expense.tracker.data.entity.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        Category::class,
        Transaction::class,
        Budget::class,
        UserPreferences::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class ExpenseDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun transactionDao(): TransactionDao
    abstract fun budgetDao(): BudgetDao
    abstract fun userPreferencesDao(): UserPreferencesDao

    companion object {
        @Volatile
        private var INSTANCE: ExpenseDatabase? = null

        fun getDatabase(context: Context): ExpenseDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ExpenseDatabase::class.java,
                    "expense_tracker_database"
                )
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateDatabase(database)
                    }
                }
            }
        }

        private suspend fun populateDatabase(database: ExpenseDatabase) {
            val categoryDao = database.categoryDao()
            val preferencesDao = database.userPreferencesDao()

            // Insert default categories
            val mainCategoryIds = mutableMapOf<String, Long>()

            // Insert main categories
            CategoryDefaults.mainCategories.forEach { categoryName ->
                val id = categoryDao.insertCategory(
                    Category(name = categoryName)
                )
                mainCategoryIds[categoryName] = id
            }

            // Insert subcategories
            CategoryDefaults.subCategories.forEach { (parentName, subCats) ->
                val parentId = mainCategoryIds[parentName]
                if (parentId != null) {
                    subCats.forEach { subCatName ->
                        categoryDao.insertCategory(
                            Category(
                                name = subCatName,
                                parentCategoryId = parentId
                            )
                        )
                    }
                }
            }

            // Insert default user preferences
            preferencesDao.insertPreferences(
                UserPreferences(
                    billingCycleStartDay = 1,
                    showPieCharts = true,
                    showSubcategoryBreakdown = true,
                    dailyReminderEnabled = true,
                    budgetAlertThreshold = 80
                )
            )
        }
    }
}
