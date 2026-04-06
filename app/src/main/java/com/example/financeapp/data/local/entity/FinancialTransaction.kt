package com.example.financeapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "financial_transactions")
data class FinancialTransaction(
    @PrimaryKey(autoGenerate = true)
    val transactionIdentifier: Int = 0,
    val transactionAmount: Double,
    val transactionType: String,
    val categoryName: String,
    val transactionDateTimestamp: Long,
    val transactionDescriptionNote: String
)