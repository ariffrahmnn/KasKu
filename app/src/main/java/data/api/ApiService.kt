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


    // --- TRANSACTIONS ---
    @GET("get_transactions.php")
    suspend fun getAllTransactions(
        @Query("user_id") userId: Int
    ): List<Transaction>

    @FormUrlEncoded
    @POST("add_transaction.php")
    suspend fun addTransaction(
        @Field("user_id") userId: Int,
        @Field("type") type: String,
        @Field("amount") amount: Double,
        @Field("description") description: String,
        @Field("date") date: String,
        @Field("timestamp") timestamp: Long
    ): TransactionResponse

    // --- PRODUCTS ---
    @GET("get_products.php")
    suspend fun getAllProducts(
        @Query("user_id") userId: Int
    ): ProductResponse

    @FormUrlEncoded
    @POST("add_product.php")
    suspend fun addProduct(
        @Field("user_id") userId: Int,
        @Field("name") name: String,
        @Field("category") category: String,
        @Field("stock") stock: Int,
        @Field("unit") unit: String,
        @Field("purchase_price") purchasePrice: Double,
        @Field("selling_price") sellingPrice: Double
    ): TransactionResponse

    // --- PURCHASES ---
    @FormUrlEncoded
    @POST("add_purchase.php")
    suspend fun addPurchase(
        @Field("user_id") userId: Int,
        @Field("invoice_number") invoiceNumber: String,
        @Field("supplier_name") supplierName: String,
        @Field("product_id") productId: Int,
        @Field("product_name") productName: String,
        @Field("category") category: String,
        @Field("quantity") quantity: Int,
        @Field("unit") unit: String,
        @Field("price_per_unit") pricePerUnit: Double,
        @Field("selling_price") sellingPrice: Double,
        @Field("extra_cost") extraCost: Double,
        @Field("payment_status") paymentStatus: String,
        @Field("timestamp") timestamp: Long
    ): TransactionResponse

    // --- SALES ---
    @FormUrlEncoded
    @POST("add_sale.php")
    suspend fun addSale(
        @Field("user_id") userId: Int,
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

    // Hapus semua transaksi per user
    @FormUrlEncoded
    @POST("delete_all_transactions.php")
    suspend fun deleteAllTransactions(
        @Field("user_id") userId: Int
    ): TransactionResponse
}