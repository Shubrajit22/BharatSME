package com.example.bharatsme.data.remote.dto

data class UserProfile(
    val fullName: String,
    val email: String,
    val username: String, // e.g., SME-1 or APPLICANT-5
    val userType: String, // INDIVIDUAL or SME
    val kycStatus: String, // PENDING, VERIFIED, REJECTED
    val createdAt: String
)
