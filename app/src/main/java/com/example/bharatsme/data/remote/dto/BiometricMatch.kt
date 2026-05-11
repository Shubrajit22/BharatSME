package com.example.bharatsme.data.remote.dto

import com.google.gson.annotations.SerializedName

data class BiometricMatch(
    @SerializedName("id")
    val applicantId: String,

    @SerializedName("name")
    val fullName: String,

    @SerializedName("score")
    val similarityScore: Float
)