package com.example.financeapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "savings_goals")
data class SavingsGoal(
    @PrimaryKey(autoGenerate = true)
    val savingsGoalIdentifier: Int = 0,
    val goalTitleName: String,
    val targetSavingsAmount: Double,
    val currentlySavedAmount: Double,
    val goalDeadlineTimestamp: Long
)