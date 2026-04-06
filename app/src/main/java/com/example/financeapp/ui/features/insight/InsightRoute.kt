package com.example.financeapp.ui.features.insight

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun InsightRoute(viewModel: InsightViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val timeframe by viewModel.selectedTimeframe.collectAsStateWithLifecycle()

    InsightScreenContent(
        uiState = uiState,
        currentTimeframe = timeframe,
        onTimeframeChange = viewModel::updateTimeframe
    )
}