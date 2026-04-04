package com.example.financeapp.data.models

import androidx.compose.ui.graphics.Color

data class CategorySpendingSummary(
    val categoryName: String,
    val totalAmountSpent: Double,
    val categoryIndicatorColor: Color
)