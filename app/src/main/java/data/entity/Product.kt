package com.example.kasku.data.entity

import com.google.gson.annotations.SerializedName

data class Product(
    @SerializedName("id")
    val id: Int = 0,
    
    @SerializedName("name")
    val name: String,
    
    @SerializedName("category")
    val category: String,
    
    @SerializedName("stock")
    val stock: Int,

    @SerializedName("unit")
    val unit: String = "Pcs",

    @SerializedName("purchase_price")
    val purchasePrice: Double,
    
    @SerializedName("selling_price")
    val sellingPrice: Double
)