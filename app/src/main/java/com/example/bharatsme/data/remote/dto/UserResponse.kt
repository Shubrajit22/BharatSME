package com.example.bharatsme.data.remote.dto

data class UserResponse(
    val id: Int,
    val username: String,
    val email: String,
    val fullName: String,
    val role: String
)