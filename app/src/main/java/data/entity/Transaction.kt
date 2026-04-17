package com.example.kasku.data.entity // Pastikan package sesuai dengan struktur barumu

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    @SerializedName("id")
    val id: Int = 0,

    @SerializedName("type")
    val type: String,        // "Pemasukan" atau "Pengeluaran"

    @SerializedName("amount")
    val amount: Double,

    @SerializedName("description")
    val description: String,

    @SerializedName("date")
    val date: String,        // Format: "yyyy-MM-dd"

    @SerializedName("timestamp")
    val timestamp: Long
)