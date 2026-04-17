package com.example.kasku.data.api

import com.google.gson.annotations.SerializedName

data class TransactionResponse(
    @SerializedName("status")
    val status: String,
    @SerializedName("message")
    val message: String
)