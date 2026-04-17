package com.example.kasku.data.api

import com.example.kasku.data.entity.Product
import com.example.kasku.data.entity.Transaction
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @FormUrlEncoded
    @POST("login.php")
    fun loginUser(
        @Field("username") username: String,
        @Field("password") password: String
    ): Call<LoginResponse>

    @FormUrlEncoded
    @POST("register.php")
    fun registerUser(
        @Field("username") username: String,
        @Field("password") password: String,
        @Field("email") email: String
    ): Call<LoginResponse>


    // --- TRANSACTIONS (PENGGANTI DAO) ---
    @GET("get_transactions.php")
    suspend fun getAllTransactions(): List<Transaction>

    @FormUrlEncoded
    @POST("add_transaction.php")
    suspend fun addTransaction(
        @Field("type") type: String,
        @Field("amount") amount: Double,
        @Field("description") description: String,
        @Field("date") date: String,
        @Field("timestamp") timestamp: Long
    ): TransactionResponse

    // --- PRODUCTS ---
    @GET("get_products.php")
    suspend fun getAllProducts(): ProductResponse

    // --- PURCHASES (PEMBELIAN) ---
    @FormUrlEncoded
    @POST("add_purchase.php")
    suspend fun addPurchase(
        @Field("invoice_number") invoiceNumber: String,
        @Field("supplier_name") supplierName: String,
        @Field("product_name") productName: String,
        @Field("product_id") productId: Int,
        @Field("quantity") quantity: Int,
        @Field("price_per_unit") pricePerUnit: Double,
        @Field("extra_cost") extraCost: Double,
        @Field("payment_status") paymentStatus: String,
        @Field("timestamp") timestamp: Long
    ): TransactionResponse

    // --- SALES (PENJUALAN) ---
    @FormUrlEncoded
    @POST("add_sale.php")
    suspend fun addSale(
        @Field("receipt_number") receiptNumber: String,
        @Field("product_name") productName: String,
        @Field("product_id") productId: Int,
        @Field("quantity") quantity: Int,
        @Field("sale_price") salePrice: Double,
        @Field("payment_method") paymentMethod: String,
        @Field("customer_info") customerInfo: String,
        @Field("cashier_name") cashierName: String,
        @Field("timestamp") timestamp: Long
    ): TransactionResponse

    // Hapus transaksi
    @FormUrlEncoded
    @POST("delete_transaction.php")
    suspend fun deleteTransaction(
        @Field("id") id: Int
    ): TransactionResponse

    // Hapus semua transaksi
    @POST("delete_all_transactions.php")
    suspend fun deleteAllTransactions(): TransactionResponse
}