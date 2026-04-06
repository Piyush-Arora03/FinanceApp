package com.example.financeapp.data

import android.content.Context
import androidx.room.Room
import com.example.financeapp.data.local.FinanceDatabase
import com.example.financeapp.data.local.dao.GoalDao
import com.example.financeapp.data.local.dao.TransactionDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideFinanceDatabase(@ApplicationContext applicationContext: Context): FinanceDatabase {
        return Room.databaseBuilder(
            applicationContext,
            FinanceDatabase::class.java,
            "finance_companion_database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideTransactionDao(financeDatabase: FinanceDatabase): TransactionDao {
        return financeDatabase.retrieveTransactionDao()
    }

    @Provides
    @Singleton
    fun provideGoalDao(financeDatabase: FinanceDatabase): GoalDao {
        return financeDatabase.retrieveGoalDao()
    }
}