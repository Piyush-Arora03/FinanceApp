package com.example.financeapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "goal_contributions")
data class GoalContribution(
    @PrimaryKey(autoGenerate = true)
    val contributionIdentifier: Int = 0,
    val parentGoalIdentifier: Int,
    val contributionAmount: Double,
    val contributionTimestamp: Long
)