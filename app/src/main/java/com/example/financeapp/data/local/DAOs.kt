package com.example.financeapp.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.financeapp.data.local.FinancialTransaction
import com.example.financeapp.data.local.SavingsGoal
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
}

@Dao
interface GoalDao {
    @Query("SELECT * FROM savings_goals ORDER BY goalDeadlineTimestamp ASC")
    fun observeAllSavingsGoals(): Flow<List<SavingsGoal>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNewSavingsGoal(savingsGoal: SavingsGoal)

    @Update
    suspend fun updateExistingSavingsGoal(savingsGoal: SavingsGoal)

    @Delete
    suspend fun deleteSavingsGoal(savingsGoal: SavingsGoal)
}