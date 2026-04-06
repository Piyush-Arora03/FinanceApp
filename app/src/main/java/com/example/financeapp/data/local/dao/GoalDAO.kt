package com.example.financeapp.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.financeapp.data.local.entity.GoalContribution
import com.example.financeapp.data.local.entity.SavingsGoal
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalDao {
    // --- Goal Queries ---
    @Query("SELECT * FROM savings_goals ORDER BY goalDeadlineTimestamp ASC")
    fun observeAllSavingsGoals(): Flow<List<SavingsGoal>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNewSavingsGoal(savingsGoal: SavingsGoal)

    @Update
    suspend fun updateExistingSavingsGoal(savingsGoal: SavingsGoal)

    @Delete
    suspend fun deleteSavingsGoal(savingsGoal: SavingsGoal)

    // --- Contribution Queries (Option B) ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoalContribution(goalContribution: GoalContribution)

    @Query("SELECT * FROM goal_contributions ORDER BY contributionTimestamp DESC")
    fun observeAllGoalContributions(): Flow<List<GoalContribution>>
}