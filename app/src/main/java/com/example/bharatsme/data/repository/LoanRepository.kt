package com.example.bharatsme.data.repository

import com.example.bharatsme.data.remote.api.SmeApiService
import com.example.bharatsme.data.remote.dto.LoanCreate
import com.example.bharatsme.data.remote.dto.LoanResponse
import com.example.bharatsme.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LoanRepository(private val api: SmeApiService) {

    // Fetch existing applications
    suspend fun getApplications(): Resource<List<LoanResponse>> {
        return try {
            val response = api.listApps() // Calls GET /api/v1/loans/

            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                // Include the status code in the error message for the ViewModel to catch
                Resource.Error("Error ${response.code()}: ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Network Connection Error")
        }
    }

    // CREATE the new application (Fixes your unresolved reference)
    suspend fun createApp(request: LoanCreate): Resource<LoanResponse> = withContext(Dispatchers.IO) {
        try {
            val response = api.createApp(request)
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error("Submission failed: ${response.code()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Connection error to backend")
        }
    }

    suspend fun evaluateLoan(id: String) = withContext(Dispatchers.IO) {
        try {
            api.runPrescreen(id)
        } catch (e: Exception) {
            // Handle error
        }
    }
}