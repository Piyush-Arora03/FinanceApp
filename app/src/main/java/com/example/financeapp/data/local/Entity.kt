package com.example.financeapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "financial_transactions")
data class FinancialTransaction(
    @PrimaryKey(autoGenerate = true)
    val transactionIdentifier: Int = 0,
    val transactionAmount: Double,
    val transactionType: String,
    val categoryName: String,
    val transactionDateTimestamp: Long,
    val transactionDescriptionNote: String
)

@Entity(tableName = "savings_goals")
data class SavingsGoal(
    @PrimaryKey(autoGenerate = true)
    val savingsGoalIdentifier: Int = 0,
    val goalTitleName: String,
    val targetSavingsAmount: Double,
    val currentlySavedAmount: Double,
    val goalDeadlineTimestamp: Long
)