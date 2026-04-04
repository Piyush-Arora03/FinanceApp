package com.example.financeapp.ui.features.transaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financeapp.data.local.TransactionDao
import com.example.financeapp.data.local.FinancialTransaction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val transactionDao: TransactionDao
) : ViewModel() {

    private val _searchQueryText = MutableStateFlow("")
    val searchQueryText = _searchQueryText.asStateFlow()

    private val _activeFilterType = MutableStateFlow("All")
    val activeFilterType = _activeFilterType.asStateFlow()

    val filteredTransactionsState = combine(
        transactionDao.observeAllTransactions(),
        _searchQueryText,
        _activeFilterType
    ) { allDatabaseTransactions, currentSearchText, currentFilterSelection ->
        var processedTransactionsList = allDatabaseTransactions

        if (currentFilterSelection != "All") {
            processedTransactionsList = processedTransactionsList.filter { transactionItem ->
                transactionItem.transactionType.equals(currentFilterSelection, ignoreCase = true)
            }
        }

        if (currentSearchText.isNotBlank()) {
            processedTransactionsList = processedTransactionsList.filter { transactionItem ->
                transactionItem.categoryName.contains(currentSearchText, ignoreCase = true) ||
                        transactionItem.transactionDescriptionNote.contains(currentSearchText, ignoreCase = true)
            }
        }

        processedTransactionsList
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun updateSearchQueryText(newSearchQuery: String) {
        _searchQueryText.value = newSearchQuery
    }

    fun updateActiveFilterType(newFilterType: String) {
        _activeFilterType.value = newFilterType
    }

    fun deleteSpecificTransaction(transactionToDelete: FinancialTransaction) {
        viewModelScope.launch {
            transactionDao.deleteTransaction(transactionToDelete)
        }
    }

    fun saveNewTransaction(
        amount: Double,
        type: String,
        category: String,
        description: String
    ) {
        viewModelScope.launch {
            val newTransaction = FinancialTransaction(
                transactionAmount = amount,
                transactionType = type,
                categoryName = category,
                transactionDateTimestamp = System.currentTimeMillis(), // Defaults to right now
                transactionDescriptionNote = description
            )
            transactionDao.insertNewTransaction(newTransaction)
        }
    }
}