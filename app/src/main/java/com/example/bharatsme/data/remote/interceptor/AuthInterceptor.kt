package com.example.bharatsme.data.remote.interceptor

import com.example.bharatsme.data.local.TokenManager
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val tokenManager: TokenManager) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        // runBlocking is used here to bridge the gap between
        // DataStore's async Flow and OkHttp's sync Interceptor.
        val token = runBlocking {
            tokenManager.getToken().firstOrNull()
        }

        val request = chain.request().newBuilder()

        // If token isn't null, add it to the headers
        token?.let {
            request.addHeader("Authorization", "Bearer $it")
        }

        return chain.proceed(request.build())
    }
}