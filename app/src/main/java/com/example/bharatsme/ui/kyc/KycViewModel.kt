package com.example.bharatsme.ui.kyc

import android.net.Uri
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bharatsme.data.repository.KycRepository
import com.example.bharatsme.util.Resource
import kotlinx.coroutines.launch

enum class KycStep { BASIC, PAN, AADHAAR, BIOMETRICS, SUBMITTED }

class KycViewModel(private val repository: KycRepository) : ViewModel() {

    private val _currentStep = mutableStateOf(KycStep.BASIC)
    val currentStep: State<KycStep> = _currentStep

    private val _kycState = mutableStateOf<Resource<Unit>?>(null)
    val kycState: State<Resource<Unit>?> = _kycState

    // State for the data
    var applicationId = mutableStateOf("") // You'll need to set this after Init
    var panNumber = mutableStateOf("")
    var panUri = mutableStateOf<Uri?>(null)
    // Add these to KycViewModel.kt
    var aadhaarNumber = mutableStateOf("")
    var aadhaarFrontUri = mutableStateOf<Uri?>(null)
    var aadhaarBackUri = mutableStateOf<Uri?>(null)

    var selfieUri = mutableStateOf<Uri?>(null)
    var signatureUri = mutableStateOf<Uri?>(null)

    fun moveToNextStep() {
        val nextIndex = _currentStep.value.ordinal + 1
        if (nextIndex < KycStep.entries.size) {
            _currentStep.value = KycStep.entries[nextIndex]
        }
    }

    fun submitBasicDetails(name: String, email: String) {
        viewModelScope.launch {
            _kycState.value = Resource.Loading()

            val result = repository.initKyc(name, email)

            // Use the <*> here to satisfy the compiler
            if (result is Resource.Success<*>) {
                // Logic to move forward
                applicationId.value = "APP-${System.currentTimeMillis()}"
                moveToNextStep()
            }

            _kycState.value = result
        }
    }

    fun submitAadhaar() {
        viewModelScope.launch {
            _kycState.value = Resource.Loading()
            val result = repository.validateAadhaar(
                appId = applicationId.value,
                aadhaarNum = aadhaarNumber.value,
                frontUri = aadhaarFrontUri.value!!,
                backUri = aadhaarBackUri.value!!
            )
            if (result is Resource.Success) moveToNextStep()
            _kycState.value = result
        }
    }
}