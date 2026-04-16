package com.example.kasku.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val type: String,        // "Pemasukan" atau "Pengeluaran"
    val amount: Double,
    val description: String,
    val date: String,        // Format: "yyyy-MM-dd"
    val timestamp: Long = System.currentTimeMillis()
)