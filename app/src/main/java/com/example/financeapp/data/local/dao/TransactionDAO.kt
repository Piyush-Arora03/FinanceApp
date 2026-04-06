package com.example.financeapp.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.financeapp.data.local.entity.FinancialTransaction
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Query("SELECT * FROM financial_transactions ORDER BY transactionDateTimestamp DESC")
    fun observeAllTransactions(): Flow<List<FinancialTransaction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNewTransaction(financialTransaction: FinancialTransaction)

    @Update
    suspend fun updateExistingTransaction(financialTransaction: FinancialTransaction)

    @Delete
    suspend fun deleteTransaction(financialTransaction: FinancialTransaction)

    @Query("SELECT * FROM financial_transactions WHERE transactionDateTimestamp BETWEEN :startTimestamp AND :endTimestamp ORDER BY transactionDateTimestamp DESC")
    fun observeTransactionsBetween(startTimestamp: Long, endTimestamp: Long): kotlinx.coroutines.flow.Flow<List<FinancialTransaction>>
}