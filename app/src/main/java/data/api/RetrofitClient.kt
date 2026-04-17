package com.example.kasku.data.api

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    // JIKA PAKAI EMULATOR: gunakan "http://10.0.2.2/api_dbkasku/"
    // JIKA PAKAI HP ASLI: gunakan IP laptop, misal "http://192.168.1.15/api_dbkasku/"
    private const val BASE_URL = "http://10.0.2.2/api_dbkasku/"

    val instance: ApiService by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        val gson = GsonBuilder()
            .setLenient() // Mencegah crash jika PHP mengirim warning/notice
            .create()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        retrofit.create(ApiService::class.java)
    }
}