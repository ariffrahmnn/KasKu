package com.example.kasku.viewmodel

import android.app.Application
import android.content.Context
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
    private val prefs = application.getSharedPreferences("kasku_prefs", Context.MODE_PRIVATE)

    private val _allTransactions = MutableStateFlow<List<Transaction>>(emptyList())
    val allTransactions: StateFlow<List<Transaction>> = _allTransactions.asStateFlow()

    private val _totalIncome = MutableStateFlow<Double>(100000.0)
    val totalIncome: StateFlow<Double> = _totalIncome.asStateFlow()

    private val _totalExpense = MutableStateFlow<Double>(0.0)
    val totalExpense: StateFlow<Double> = _totalExpense.asStateFlow()

    private val _allProducts = MutableStateFlow<List<Product>>(emptyList())
    val allProducts: StateFlow<List<Product>> = _allProducts.asStateFlow()

    private fun getUserId(): Int {
        return prefs.getInt("user_id", -1)
    }

    init {
        val userId = getUserId()
        if (userId != -1) {
            fetchTransactions()
            fetchProducts()
        }
    }

    fun fetchTransactions() = viewModelScope.launch {
        val userId = getUserId()
        if (userId == -1) return@launch
        
        val transactions = repository.getAllTransactions(userId)
        _allTransactions.value = transactions
        calculateTotals(transactions)
    }

    fun fetchProducts() = viewModelScope.launch {
        val userId = getUserId()
        if (userId == -1) return@launch

        val products = repository.getAllProducts(userId)
        _allProducts.value = products
    }

    private fun calculateTotals(transactions: List<Transaction>) {
        var income = 100000.0 // Saldo Awal Default
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
        val userId = getUserId()
        if (userId == -1) {
            onResult(false, "Sesi berakhir, silakan login kembali")
            return@launch
        }

        try {
            val response = repository.insert(userId, transaction)
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
        val userId = getUserId()
        if (userId == -1) return@launch

        try {
            val response = repository.insertPurchase(userId, purchase)
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
        val userId = getUserId()
        if (userId == -1) return@launch

        try {
            val response = repository.insertSale(userId, sale)
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

    fun addProduct(
        name: String,
        category: String,
        stock: Int,
        unit: String,
        purchasePrice: Double,
        sellingPrice: Double,
        onResult: (Boolean, String) -> Unit
    ) = viewModelScope.launch {
        val userId = getUserId()
        if (userId == -1) return@launch

        try {
            val response = repository.addProduct(userId, name, category, stock, unit, purchasePrice, sellingPrice)
            if (response.status == "success") {
                fetchProducts()
                onResult(true, "Produk berhasil ditambahkan")
            } else {
                onResult(false, response.message)
            }
        } catch (e: Exception) {
            onResult(false, e.message ?: "Terjadi kesalahan")
        }
    }

    fun deleteAll() = viewModelScope.launch {
        val userId = getUserId()
        if (userId == -1) return@launch

        val response = repository.deleteAll(userId)
        if (response.status == "success") {
            fetchTransactions()
        }
    }
}