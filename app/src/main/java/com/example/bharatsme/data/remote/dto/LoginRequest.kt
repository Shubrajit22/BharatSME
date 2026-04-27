package com.example.bharatsme.data.remote.dto

data class LoginRequest(
    val identifier: String, // Matches your identifier logic in FastAPI
    val password: String
)