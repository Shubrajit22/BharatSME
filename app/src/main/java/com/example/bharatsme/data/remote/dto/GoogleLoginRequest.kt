package com.example.bharatsme.data.remote.dto

import com.google.gson.annotations.SerializedName

data class GoogleLoginRequest(
    @SerializedName("idToken")
    val idToken: String,

    @SerializedName("userType")
    val userType: String // "INDIVIDUAL" or "SME"
)
