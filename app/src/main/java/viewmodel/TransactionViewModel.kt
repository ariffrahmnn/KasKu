package com.example.kasku.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.kasku.data.database.AppDatabase
import com.example.kasku.data.entity.Transaction
import com.example.kasku.data.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class TransactionViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TransactionRepository

    val allTransactions: Flow<List<Transaction>>
    val totalIncome: Flow<Double?>
    val totalExpense: Flow<Double?>

    init {
        val transactionDao = AppDatabase.getDatabase(application).transactionDao()
        repository = TransactionRepository(transactionDao)
        allTransactions = repository.allTransactions
        totalIncome = repository.totalIncome
        totalExpense = repository.totalExpense
    }

    fun insert(transaction: Transaction) = viewModelScope.launch {
        repository.insert(transaction)
    }

    fun update(transaction: Transaction) = viewModelScope.launch {
        repository.update(transaction)
    }

    fun delete(transaction: Transaction) = viewModelScope.launch {
        repository.delete(transaction)
    }

    fun deleteAll() = viewModelScope.launch {
        repository.deleteAll()
    }
}