package com.example.bharatsme.data.repository

import com.example.bharatsme.data.local.TokenManager
import com.example.bharatsme.data.remote.api.SmeApiService
import com.example.bharatsme.data.remote.dto.LoginRequest
import com.example.bharatsme.data.remote.dto.RegisterRequest
import com.example.bharatsme.data.remote.dto.TokenResponse
import com.example.bharatsme.data.remote.dto.UserResponse
import com.example.bharatsme.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepository(
    private val api: SmeApiService,
    private val tokenManager: TokenManager
) {

    // Register User
    suspend fun register(request: RegisterRequest): Resource<UserResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.register(request)
                if (response.isSuccessful && response.body() != null) {
                    Resource.Success(response.body()!!)
                } else {
                    Resource.Error(response.message() ?: "Registration failed")
                }
            } catch (e: Exception) {
                Resource.Error(e.localizedMessage ?: "An unknown error occurred")
            }
        }
    }

    // Login User and Save Token
    suspend fun login(request: LoginRequest): Resource<TokenResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.login(request)
                val body = response.body()

                if (response.isSuccessful && body != null) {
                    // Save the token to DataStore immediately
                    tokenManager.saveToken(body.accessToken)
                    Resource.Success(body)
                } else {
                    // Handle 401 Unauthorized or other errors
                    Resource.Error("Invalid credentials or server error")
                }
            } catch (e: Exception) {
                Resource.Error(e.localizedMessage ?: "Network error. Check your backend.")
            }
        }
    }

    // Logout
    suspend fun logout() {
        tokenManager.deleteToken()
    }
}