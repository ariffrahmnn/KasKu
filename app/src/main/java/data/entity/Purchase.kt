package com.example.kasku.data.entity

import com.google.gson.annotations.SerializedName

data class Purchase(
    @SerializedName("id")
    val id: Int = 0,
    
    @SerializedName("invoice_number")
    val invoiceNumber: String,
    
    @SerializedName("purchase_date")
    val purchaseDate: String,
    
    @SerializedName("supplier_name")
    val supplierName: String,
    
    @SerializedName("product_id")
    val productId: Int,

    @SerializedName("product_name")
    val productName: String,
    
    @SerializedName("quantity")
    val quantity: Int,
    
    @SerializedName("price_per_unit")
    val pricePerUnit: Double,
    
    @SerializedName("extra_cost")
    val extraCost: Double,
    
    @SerializedName("payment_status")
    val paymentStatus: String,
    
    @SerializedName("timestamp")
    val timestamp: Long
)