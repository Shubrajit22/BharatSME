package com.example.bharatsme.ui.loans

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bharatsme.data.remote.dto.LoanCreate
import com.example.bharatsme.data.remote.dto.LoanResponse
import com.example.bharatsme.data.repository.LoanRepository
import com.example.bharatsme.util.Resource
import kotlinx.coroutines.launch

class LoanViewModel(private val repository: LoanRepository) : ViewModel() {

    private val _loanState = mutableStateOf<Resource<LoanResponse>?>(null)
    val loanState: State<Resource<LoanResponse>?> = _loanState

    fun applyForLoan(
        name: String,
        type: String,
        turnover: String,
        amount: String,
        years: Int
    ) {
        viewModelScope.launch {
            _loanState.value = Resource.Loading()
            val request = LoanCreate(
                applicantName = name,
                businessType = type,
                turnoverBand = turnover,
                requestedLoanAmount = amount,
                yearsInBusiness = years
            )
            _loanState.value = repository.createApp(request)
        }
    }
}