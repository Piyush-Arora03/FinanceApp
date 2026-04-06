package com.example.financeapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.financeapp.data.local.dao.GoalDao
import com.example.financeapp.data.local.dao.TransactionDao
import com.example.financeapp.data.local.entity.FinancialTransaction
import com.example.financeapp.data.local.entity.GoalContribution
import com.example.financeapp.data.local.entity.SavingsGoal

@Database(
    entities = [FinancialTransaction::class, SavingsGoal::class, GoalContribution::class],
    version = 2,
    exportSchema = false
)
abstract class FinanceDatabase : RoomDatabase() {
    abstract fun retrieveTransactionDao(): TransactionDao
    abstract fun retrieveGoalDao(): GoalDao
}