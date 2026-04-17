package com.example.kasku.data.repository

import com.example.kasku.data.api.RetrofitClient
import com.example.kasku.data.api.TransactionResponse
import com.example.kasku.data.entity.Product
import com.example.kasku.data.entity.Purchase
import com.example.kasku.data.entity.Sale
import com.example.kasku.data.entity.Transaction

class TransactionRepository {

    private val apiService = RetrofitClient.instance

    suspend fun getAllTransactions(): List<Transaction> {
        return try {
            apiService.getAllTransactions()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun insert(transaction: Transaction): TransactionResponse {
        return try {
            apiService.addTransaction(
                transaction.type,
                transaction.amount,
                transaction.description,
                transaction.date,
                transaction.timestamp
            )
        } catch (e: Exception) {
            TransactionResponse("error", e.message ?: "Unknown error")
        }
    }

    suspend fun delete(transactionId: Int): TransactionResponse {
        return try {
            apiService.deleteTransaction(transactionId)
        } catch (e: Exception) {
            TransactionResponse("error", e.message ?: "Unknown error")
        }
    }

    suspend fun deleteAll(): TransactionResponse {
        return try {
            apiService.deleteAllTransactions()
        } catch (e: Exception) {
            TransactionResponse("error", e.message ?: "Unknown error")
        }
    }

    // --- PRODUCTS ---
    suspend fun getAllProducts(): List<Product> {
        return try {
            val response = apiService.getAllProducts()
            android.util.Log.d("REPO_DEBUG", "Status: ${response.status}, Data Size: ${response.data.size}")
            if (response.status == "success") {
                response.data
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            android.util.Log.e("REPO_DEBUG", "Error fetching products: ${e.message}")
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun addProduct(
        name: String,
        category: String,
        stock: Int,
        unit: String,
        purchasePrice: Double,
        sellingPrice: Double
    ): TransactionResponse {
        return try {
            apiService.addProduct(name, category, stock, unit, purchasePrice, sellingPrice)
        } catch (e: Exception) {
            TransactionResponse("error", e.message ?: "Unknown error")
        }
    }

    // --- PURCHASES ---
    suspend fun insertPurchase(purchase: Purchase): TransactionResponse {
        return try {
            apiService.addPurchase(
                purchase.invoiceNumber,
                purchase.supplierName,
                purchase.productId,
                purchase.productName,
                purchase.category,
                purchase.quantity,
                purchase.unit,
                purchase.pricePerUnit,
                purchase.sellingPrice,
                purchase.extraCost,
                purchase.paymentStatus,
                purchase.timestamp
            )
        } catch (e: Exception) {
            TransactionResponse("error", e.message ?: "Unknown error")
        }
    }

    // --- SALES ---
    suspend fun insertSale(sale: Sale): TransactionResponse {
        return try {
            apiService.addSale(
                sale.receiptNumber,
                sale.productName,
                sale.productId,
                sale.quantity,
                sale.salePrice,
                sale.paymentMethod,
                sale.customerInfo,
                sale.cashierName,
                sale.timestamp
            )
        } catch (e: Exception) {
            TransactionResponse("error", e.message ?: "Unknown error")
        }
    }
}