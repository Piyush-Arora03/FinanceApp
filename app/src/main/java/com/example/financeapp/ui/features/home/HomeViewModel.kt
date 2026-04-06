package com.example.financeapp.ui.features.home

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financeapp.data.local.entity.FinancialTransaction
import com.example.financeapp.data.local.dao.TransactionDao
import com.example.financeapp.data.models.CategorySpendingSummary
import com.example.financeapp.data.models.DailySpendingSummary
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val transactionDao: TransactionDao
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
       loadHomeData()
    }

    fun loadHomeData() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading

            transactionDao.observeAllTransactions()
                .catch { exception ->
                    _uiState.value = HomeUiState.Error(exception.message ?: "An unknown error occurred")
                }
                .collect { allTransactions ->
                    val totalIncomeAmount = allTransactions
                        .filter { it.transactionType == "income" }
                        .sumOf { it.transactionAmount }

                    val totalExpenseAmount = allTransactions
                        .filter { it.transactionType == "expense" }
                        .sumOf { it.transactionAmount }

                    val calculatedBalance = totalIncomeAmount - totalExpenseAmount

                    val calculatedSavingsRate = if (totalIncomeAmount > 0) {
                        ((totalIncomeAmount - totalExpenseAmount) / totalIncomeAmount) * 100
                    } else {
                        0.0
                    }

                    val currentCalendarInstance = java.util.Calendar.getInstance()

                    currentCalendarInstance.set(java.util.Calendar.HOUR_OF_DAY, 0)
                    currentCalendarInstance.set(java.util.Calendar.MINUTE, 0)
                    currentCalendarInstance.set(java.util.Calendar.SECOND, 0)
                    currentCalendarInstance.set(java.util.Calendar.MILLISECOND, 0)

                    val currentDayOfWeek = currentCalendarInstance.get(java.util.Calendar.DAY_OF_WEEK)
                    val daysToSubtractForMonday = if (currentDayOfWeek == java.util.Calendar.SUNDAY) {
                        6
                    } else {
                        currentDayOfWeek - java.util.Calendar.MONDAY
                    }

                    currentCalendarInstance.add(java.util.Calendar.DAY_OF_YEAR, -daysToSubtractForMonday)

                    val startOfMondayTimestamp = currentCalendarInstance.timeInMillis
                    val millisecondsInOneFullDay = 86400000L
                    val dayOfWeekFormatter = java.text.SimpleDateFormat("EEE", java.util.Locale.getDefault())

                    val weeklySpendingSummaries = (0..6).map { dayOffsetIndex ->
                        val startOfTargetDayTimestamp = startOfMondayTimestamp + (dayOffsetIndex * millisecondsInOneFullDay)
                        val endOfTargetDayTimestamp = startOfTargetDayTimestamp + millisecondsInOneFullDay - 1

                        val totalSpentOnTargetDay = allTransactions
                            .filter {
                                it.transactionType == "expense" &&
                                        it.transactionDateTimestamp in startOfTargetDayTimestamp..endOfTargetDayTimestamp
                            }
                            .sumOf { it.transactionAmount }

                        DailySpendingSummary(
                            dayOfWeek = dayOfWeekFormatter.format(java.util.Date(startOfTargetDayTimestamp)),
                            spendingAmount = totalSpentOnTargetDay.toFloat()
                        )
                    }

                    val chartColorsList = listOf(
                        Color(0xFF8C52FF), Color(0xFF5CE1E6),
                        Color(0xFFFFB300), Color(0xFFF44336),
                        Color(0xFF4CAF50)
                    )

                    val categorySpendingSummaries = allTransactions
                        .filter { it.transactionType == "expense" }
                        .groupBy { it.categoryName }
                        .entries.mapIndexed { index, mapEntry ->
                            CategorySpendingSummary(
                                categoryName = mapEntry.key,
                                totalAmountSpent = mapEntry.value.sumOf { it.transactionAmount },
                                categoryIndicatorColor = chartColorsList[index % chartColorsList.size]
                            )
                        }
                        .sortedByDescending { it.totalAmountSpent }

                    _uiState.value = HomeUiState.Success(
                        totalCurrentBalance = calculatedBalance,
                        totalMonthlyIncome = totalIncomeAmount,
                        totalMonthlyExpenses = totalExpenseAmount,
                        savingsRatePercentage = calculatedSavingsRate,
                        recentTransactions = allTransactions.take(5),
                        weeklySpendingData = weeklySpendingSummaries,
                        categorySpendingData = categorySpendingSummaries
                    )
                }
        }
    }

    sealed class HomeUiState {
        object Loading : HomeUiState()
        data class Success(
            val totalCurrentBalance: Double = 0.0,
            val totalMonthlyIncome: Double = 0.0,
            val totalMonthlyExpenses: Double = 0.0,
            val savingsRatePercentage: Double = 0.0,
            val recentTransactions: List<FinancialTransaction> = emptyList(),
            val weeklySpendingData: List<DailySpendingSummary> = emptyList(),
            val categorySpendingData: List<CategorySpendingSummary> = emptyList(),
        ) : HomeUiState()
        data class Error(val message: String) : HomeUiState()
    }
}