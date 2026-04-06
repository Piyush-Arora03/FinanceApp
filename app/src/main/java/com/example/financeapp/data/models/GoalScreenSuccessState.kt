package com.example.financeapp.data.models

import com.example.financeapp.data.local.entity.SavingsGoal


data class GoalScreenSuccessState(
    val allActiveGoalsList: List<SavingsGoal> = emptyList(),
    val totalSavedAcrossAllGoals: Double = 0.0,
    val totalTargetAcrossAllGoals: Double = 0.0,
    val contributionHeatmapData: List<HeatmapDaySummary> = emptyList()
)