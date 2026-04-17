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
            if (response.status == "success") {
                response.data
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    // --- PURCHASES ---
    suspend fun insertPurchase(purchase: Purchase): TransactionResponse {
        return try {
            apiService.addPurchase(
                purchase.invoiceNumber,
                purchase.supplierName,
                purchase.productName,
                purchase.productId,
                purchase.quantity,
                purchase.pricePerUnit,
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