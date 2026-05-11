package com.example.bharatsme.ui.dashboard.viewmodel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bharatsme.data.local.TokenManager
import com.example.bharatsme.data.remote.dto.LoanResponse
import com.example.bharatsme.data.remote.dto.UserProfile
import com.example.bharatsme.data.repository.AuthRepository
import com.example.bharatsme.data.repository.LoanRepository
import com.example.bharatsme.util.Resource
import kotlinx.coroutines.launch

class DashboardViewModel(private val repository: LoanRepository, private val tokenManager: TokenManager, private val authRepository: AuthRepository) : ViewModel() {

    private val _uiState = mutableStateOf<Resource<List<LoanResponse>>>(Resource.Loading())
    val uiState: State<Resource<List<LoanResponse>>> = _uiState

    private val _profileState = mutableStateOf<Resource<UserProfile>>(Resource.Loading())
    val profileState: State<Resource<UserProfile>> = _profileState

    // In a real app, you'd also fetch KYC status here
    fun loadDashboardData() {
        viewModelScope.launch {
            _uiState.value = Resource.Loading()

            // Use your repository to fetch the data
            val result = repository.getApplications()

            if (result is Resource.Error && result.message?.contains("401") == true) {
                // Handle the Unauthorized error specifically
                Log.e("DASHBOARD_ERROR", "Token is invalid or expired. Logging out.")

                // 1. Clear the token from DataStore
                tokenManager.clearToken()

                // 2. Update state to reflect the error
                _uiState.value = Resource.Error("Session expired. Please login again.")

                // 3. (Optional) Trigger a navigation event to the AuthScreen
            } else {
                // Otherwise, show the data (Success or different Error)
                _uiState.value = result
            }
        }
    }
    fun evaluateLoan(id: String) {
        viewModelScope.launch {
            // Call the evaluate endpoint
            repository.evaluateLoan(id)
            // Refresh the dashboard data to show the new status
            loadDashboardData()
        }
    }

    fun loadProfile() {
        viewModelScope.launch {
            _profileState.value = Resource.Loading()
            // Assuming your repository has a getProfile() method
            _profileState.value = authRepository.getProfile()
        }
    }
}