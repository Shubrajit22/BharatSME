package com.example.bharatsme.data.remote.dto

// KycResponse.kt
data class KycResponse(
    val success: Boolean,          // Must match backend: True/False
    val status: String,            // e.g., "VERIFIED", "FLAGGED"
    val extractedId: String,       // Redacted ID (e.g., "XXXX-XXXX-1234")
    val reason: String? = null,    // Why it failed (e.g., "Blurry", "Name Mismatch")
    val fraudFlags: List<String> = emptyList()
)