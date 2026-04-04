package com.example.financeapp.ui.features.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.financeapp.ui.widgets.OnUiStateError

@Composable
fun HomeRoute(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val currentUiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (val state = currentUiState) {
        is HomeViewModel.HomeUiState.Error -> {
            OnUiStateError(
                onClick = { viewModel.loadHomeData() },
                text = state.message
            )
        }
        is HomeViewModel.HomeUiState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        is HomeViewModel.HomeUiState.Success -> {
            HomeScreen(
                totalCurrentBalance = state.totalCurrentBalance,
                totalMonthlyIncome = state.totalMonthlyIncome,
                totalMonthlyExpenses = state.totalMonthlyExpenses,
                savingsRatePercentage = state.savingsRatePercentage,
                recentTransactions = state.recentTransactions,
                weeklySpendingData = state.weeklySpendingData,
                categorySpendingData = state.categorySpendingData
            )
        }
    }
}