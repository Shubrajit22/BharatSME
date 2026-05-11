package com.example.bharatsme.data.remote.dto

import com.google.gson.annotations.SerializedName

data class BiometricSearchResponse(
    @SerializedName("matches")
    val matches: List<BiometricMatch>
)