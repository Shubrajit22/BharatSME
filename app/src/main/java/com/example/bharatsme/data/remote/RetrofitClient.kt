package com.example.bharatsme.data.remote

import com.example.bharatsme.data.local.TokenManager
import com.example.bharatsme.data.remote.api.SmeApiService
import com.example.bharatsme.data.remote.interceptor.AuthInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.jvm.java

object RetrofitClient {
    private const val BASE_URL = "http://10.0.2.2:8000/"

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // You will pass your TokenManager here once it's initialized
    private fun createOkHttpClient(tokenManager: TokenManager? = null): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .apply {
                if (tokenManager != null) {
                    addInterceptor(AuthInterceptor(tokenManager))
                }
            }
            .build()
    }

    fun getApiService(tokenManager: TokenManager? = null): SmeApiService {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(createOkHttpClient(tokenManager))
            .build()
            .create(SmeApiService::class.java)
    }
}