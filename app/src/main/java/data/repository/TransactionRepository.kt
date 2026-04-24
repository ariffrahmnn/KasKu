package com.example.kasku.data.repository

import com.example.kasku.data.api.RetrofitClient
import com.example.kasku.data.api.TransactionResponse
import com.example.kasku.data.entity.Product
import com.example.kasku.data.entity.Purchase
import com.example.kasku.data.entity.Sale
import com.example.kasku.data.entity.Transaction

class TransactionRepository {

    private val apiService = RetrofitClient.instance

    suspend fun getAllTransactions(userId: Int): List<Transaction> {
        return try {
            apiService.getAllTransactions(userId)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun insert(userId: Int, transaction: Transaction): TransactionResponse {
        return try {
            apiService.addTransaction(
                userId,
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

    suspend fun deleteAll(userId: Int): TransactionResponse {
        return try {
            apiService.deleteAllTransactions(userId)
        } catch (e: Exception) {
            TransactionResponse("error", e.message ?: "Unknown error")
        }
    }

    // --- PRODUCTS ---
    suspend fun getAllProducts(userId: Int): List<Product> {
        return try {
            val response = apiService.getAllProducts(userId)
            if (response.status == "success") {
                response.data
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun addProduct(
        userId: Int,
        name: String,
        category: String,
        stock: Int,
        unit: String,
        purchasePrice: Double,
        sellingPrice: Double
    ): TransactionResponse {
        return try {
            apiService.addProduct(userId, name, category, stock, unit, purchasePrice, sellingPrice)
        } catch (e: Exception) {
            TransactionResponse("error", e.message ?: "Unknown error")
        }
    }

    // --- PURCHASES ---
    suspend fun insertPurchase(userId: Int, purchase: Purchase): TransactionResponse {
        return try {
            apiService.addPurchase(
                userId,
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
    suspend fun insertSale(userId: Int, sale: Sale): TransactionResponse {
        return try {
            apiService.addSale(
                userId,
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