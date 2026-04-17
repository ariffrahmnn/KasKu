package com.example.kasku.data.entity

import com.google.gson.annotations.SerializedName

data class Sale(
    @SerializedName("id")
    val id: Int = 0,
    
    @SerializedName("receipt_number")
    val receiptNumber: String,
    
    @SerializedName("product_id")
    val productId: Int,

    @SerializedName("product_name")
    val productName: String,
    
    @SerializedName("quantity")
    val quantity: Int,
    
    @SerializedName("sale_price")
    val salePrice: Double,
    
    @SerializedName("payment_method")
    val paymentMethod: String,
    
    @SerializedName("customer_info")
    val customerInfo: String,
    
    @SerializedName("cashier_name")
    val cashierName: String,
    
    @SerializedName("timestamp")
    val timestamp: Long
)