package com.example.financeapp.ui.features.goal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financeapp.data.local.dao.GoalDao
import com.example.financeapp.data.local.entity.GoalContribution
import com.example.financeapp.data.local.entity.SavingsGoal
import com.example.financeapp.data.models.GoalScreenSuccessState
import com.example.financeapp.data.models.HeatmapDaySummary
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class GoalViewModel @Inject constructor(private val dao: GoalDao) : ViewModel() {

    val uiState: StateFlow<GoalScreenUIStates> = combine(dao.observeAllSavingsGoals(), dao.observeAllGoalContributions()) { goals, contributions ->
        // Keep totals based on ALL goals (so your top progress bar doesn't shrink)
        val saved = goals.sumOf { it.currentlySavedAmount }
        val target = goals.sumOf { it.targetSavingsAmount }

        // Filter out completed goals for the list
        val activeGoalsOnly = goals.filter { it.currentlySavedAmount < it.targetSavingsAmount }

        val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val heatmap = contributions.groupBy { fmt.format(Date(it.contributionTimestamp)) }
            .map { (date, cont) -> HeatmapDaySummary(date, cont.sumOf { it.contributionAmount }) }

        GoalScreenUIStates.Success(GoalScreenSuccessState(activeGoalsOnly, saved, target, heatmap)) as GoalScreenUIStates
    }
        .catch { emit(GoalScreenUIStates.Error(it.message ?: "Failed to load goals")) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), GoalScreenUIStates.Loading)
    fun createGoal(title: String, targetAmt: Double, deadline: Long) {
        viewModelScope.launch {
            dao.insertNewSavingsGoal(SavingsGoal(
                goalTitleName = title,
                targetSavingsAmount = targetAmt,
                currentlySavedAmount = 0.0,
                goalDeadlineTimestamp = deadline
            ))
        }
    }

    fun addFunds(goal: SavingsGoal, amt: Double) {
        viewModelScope.launch {
            val remaining = goal.targetSavingsAmount - goal.currentlySavedAmount

            val safeAmt = minOf(amt, remaining)

            if (safeAmt <= 0) return@launch

            dao.insertGoalContribution(GoalContribution(
                parentGoalIdentifier = goal.savingsGoalIdentifier,
                contributionAmount = safeAmt,
                contributionTimestamp = System.currentTimeMillis()
            ))

            dao.updateExistingSavingsGoal(goal.copy(currentlySavedAmount = goal.currentlySavedAmount + safeAmt))
        }
    }

    sealed class GoalScreenUIStates {
        data class Success(val state: GoalScreenSuccessState): GoalScreenUIStates()
        data class Error(val msg: String): GoalScreenUIStates()
        object Loading: GoalScreenUIStates()
    }
}