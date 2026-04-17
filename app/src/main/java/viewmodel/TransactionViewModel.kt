package com.example.kasku.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.kasku.data.entity.Product
import com.example.kasku.data.entity.Purchase
import com.example.kasku.data.entity.Sale
import com.example.kasku.data.entity.Transaction
import com.example.kasku.data.repository.TransactionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TransactionViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = TransactionRepository()

    private val _allTransactions = MutableStateFlow<List<Transaction>>(emptyList())
    val allTransactions: StateFlow<List<Transaction>> = _allTransactions.asStateFlow()

    private val _totalIncome = MutableStateFlow<Double>(0.0)
    val totalIncome: StateFlow<Double> = _totalIncome.asStateFlow()

    private val _totalExpense = MutableStateFlow<Double>(0.0)
    val totalExpense: StateFlow<Double> = _totalExpense.asStateFlow()

    private val _allProducts = MutableStateFlow<List<Product>>(emptyList())
    val allProducts: StateFlow<List<Product>> = _allProducts.asStateFlow()

    init {
        fetchTransactions()
        fetchProducts()
    }

    fun fetchTransactions() = viewModelScope.launch {
        val transactions = repository.getAllTransactions()
        _allTransactions.value = transactions
        calculateTotals(transactions)
    }

    fun fetchProducts() = viewModelScope.launch {
        val products = repository.getAllProducts()
        _allProducts.value = products
    }

    private fun calculateTotals(transactions: List<Transaction>) {
        var income = 0.0
        var expense = 0.0
        for (transaction in transactions) {
            if (transaction.type == "Pemasukan") {
                income += transaction.amount
            } else {
                expense += transaction.amount
            }
        }
        _totalIncome.value = income
        _totalExpense.value = expense
    }

    fun insert(transaction: Transaction, onResult: (Boolean, String) -> Unit) = viewModelScope.launch {
        try {
            val response = repository.insert(transaction)
            if (response.status == "success") {
                fetchTransactions()
                onResult(true, "Berhasil")
            } else {
                onResult(false, response.message)
            }
        } catch (e: Exception) {
            onResult(false, e.message ?: "Terjadi kesalahan")
        }
    }

    fun insertPurchase(purchase: Purchase, onResult: (Boolean, String) -> Unit) = viewModelScope.launch {
        try {
            val response = repository.insertPurchase(purchase)
            if (response.status == "success") {
                fetchProducts()
                fetchTransactions()
                onResult(true, "Pembelian berhasil dicatat")
            } else {
                onResult(false, response.message)
            }
        } catch (e: Exception) {
            onResult(false, e.message ?: "Terjadi kesalahan")
        }
    }

    fun insertSale(sale: Sale, onResult: (Boolean, String) -> Unit) = viewModelScope.launch {
        try {
            val response = repository.insertSale(sale)
            if (response.status == "success") {
                fetchProducts()
                fetchTransactions()
                onResult(true, "Penjualan berhasil dicatat")
            } else {
                onResult(false, response.message)
            }
        } catch (e: Exception) {
            onResult(false, e.message ?: "Terjadi kesalahan")
        }
    }

    fun delete(transaction: Transaction) = viewModelScope.launch {
        val response = repository.delete(transaction.id)
        if (response.status == "success") {
            fetchTransactions()
        }
    }

    fun deleteAll() = viewModelScope.launch {
        val response = repository.deleteAll()
        if (response.status == "success") {
            fetchTransactions()
        }
    }
}