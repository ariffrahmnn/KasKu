package com.example.kasku.data.api

data class LoginResponse(
    val status: String,
    val message: String,
    val userId: Int? = null,
    val username: String? = null
)