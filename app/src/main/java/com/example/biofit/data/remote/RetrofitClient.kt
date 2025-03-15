package com.example.biofit.data.remote

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private const val BASE_URL = "http://192.168.100.33:8080/"

    // Tạo OkHttpClient để có thể thiết lập timeout
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    // Tạo và cache instance của Retrofit
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client) // sử dụng OkHttp
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Instance của ApiService
    val instance: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }

    // Instance của AuthApiService
    val authInstance : AuthApiService by lazy {
        retrofit.create(AuthApiService::class.java)
    }

}