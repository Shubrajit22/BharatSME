package com.example.bharatsme.data.remote.dto

data class KycInitResponse(
    val isDuplicate: Boolean,
    val applicationId: String,
    val status: String? = null // Backend returns status if it's a duplicate
)
