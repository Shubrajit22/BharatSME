package com.example.bharatsme.ui.dashboard

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bharatsme.data.remote.dto.LoanResponse
import com.example.bharatsme.data.repository.LoanRepository
import com.example.bharatsme.util.Resource
import kotlinx.coroutines.launch

class DashboardViewModel(private val repository: LoanRepository) : ViewModel() {

    private val _uiState = mutableStateOf<Resource<List<LoanResponse>>>(Resource.Loading())
    val uiState: State<Resource<List<LoanResponse>>> = _uiState

    // In a real app, you'd also fetch KYC status here
    fun loadDashboardData() {
        viewModelScope.launch {
            _uiState.value = Resource.Loading()
            _uiState.value = repository.getApplications()
        }
    }
}