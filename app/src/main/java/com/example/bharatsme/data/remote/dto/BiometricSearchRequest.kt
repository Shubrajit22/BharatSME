package com.example.bharatsme.data.remote.dto

import com.google.gson.annotations.SerializedName

data class BiometricSearchRequest(
    @SerializedName("embedding")
    val embedding: List<Float>,

    @SerializedName("top_k")
    val topK: Int = 5
)