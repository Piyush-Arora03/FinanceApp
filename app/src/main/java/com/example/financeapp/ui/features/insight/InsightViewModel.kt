package com.example.financeapp.ui.features.insight

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financeapp.data.local.dao.TransactionDao
import com.example.financeapp.data.local.entity.FinancialTransaction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import java.util.Calendar
import javax.inject.Inject

enum class Timeframe { MONTH, YEAR }

data class CategorySlice(val category: String, val amount: Double, val color: Color)
data class InsightUiState(
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val categoryBreakdown: List<CategorySlice> = emptyList(),
    val topExpenseCategory: String = "None"
)

@HiltViewModel
class InsightViewModel @Inject constructor(private val dao: TransactionDao) : ViewModel() {

    private val _selectedTimeframe = MutableStateFlow(Timeframe.MONTH)
    val selectedTimeframe = _selectedTimeframe.asStateFlow()

    private val flatColors = listOf(
        Color(0xFF1976D2), // priBlu
        Color(0xFF42A5F5), // secBlu
        Color(0xFFE53935), // Red
        Color(0xFFFFB300), // Amber
        Color(0xFF43A047)  // Green
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<InsightUiState> = _selectedTimeframe.flatMapLatest { timeframe ->
        val calendar = Calendar.getInstance()

        // Reset to start of day
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.clear(Calendar.MINUTE)
        calendar.clear(Calendar.SECOND)
        calendar.clear(Calendar.MILLISECOND)

        val endTimestamp = System.currentTimeMillis()
        val startTimestamp = if (timeframe == Timeframe.MONTH) {
            calendar.set(Calendar.DAY_OF_MONTH, 1)
            calendar.timeInMillis
        } else {
            calendar.set(Calendar.DAY_OF_YEAR, 1)
            calendar.timeInMillis
        }

        dao.observeTransactionsBetween(startTimestamp, endTimestamp).map { transactions ->
            processChartData(transactions)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), InsightUiState())

    fun updateTimeframe(newTimeframe: Timeframe) {
        _selectedTimeframe.value = newTimeframe
    }

    private fun processChartData(transactions: List<FinancialTransaction>): InsightUiState {
        val income = transactions.filter { it.transactionType == "income" }.sumOf { it.transactionAmount }
        val expenses = transactions.filter { it.transactionType == "expense" }
        val totalExpense = expenses.sumOf { it.transactionAmount }

        val grouped = expenses.groupBy { it.categoryName }
            .map { (name, list) -> name to list.sumOf { it.transactionAmount } }
            .sortedByDescending { it.second }

        val slices = grouped.mapIndexed { index, pair ->
            CategorySlice(category = pair.first, amount = pair.second, color = flatColors[index % flatColors.size])
        }

        val topCategory = grouped.firstOrNull()?.first ?: "None"

        return InsightUiState(
            totalIncome = income,
            totalExpense = totalExpense,
            categoryBreakdown = slices,
            topExpenseCategory = topCategory
        )
    }
}