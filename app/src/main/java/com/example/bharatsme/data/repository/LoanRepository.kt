package com.example.bharatsme.data.repository

import com.example.bharatsme.data.remote.api.SmeApiService
import com.example.bharatsme.util.Resource

class LoanRepository(private val api: SmeApiService) {
    suspend fun getApplications() = try {
        val response = api.listApps()
        if (response.isSuccessful) Resource.Success(response.body()!!)
        else Resource.Error("Failed to fetch loans")
    } catch (e: Exception) { Resource.Error(e.message ?: "Network error") }

    suspend fun evaluateLoan(id: String) = api.runPrescreen(id)
}