package com.example.kasku.data.api

data class ProductResponse(
    val status: String,
    val data: List<Product>
)

data class Product(
    val id: Int,
    val sku: String,
    val nama_barang: String,
    val kategori: String,
    val stok: Int,
    val harga_beli: Double,
    val harga_jual: Double
)