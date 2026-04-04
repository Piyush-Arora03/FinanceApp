package com.example.financeapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [FinancialTransaction::class, SavingsGoal::class],
    version = 1,
    exportSchema = false
)
abstract class FinanceDatabase : RoomDatabase() {
    abstract fun retrieveTransactionDao(): TransactionDao
    abstract fun retrieveGoalDao(): GoalDao
}