package com.example.bharatsme.ui.kyc

import android.net.Uri
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bharatsme.data.repository.KycRepository
import com.example.bharatsme.util.NetworkResponse
import com.example.bharatsme.util.Resource
import kotlinx.coroutines.launch

enum class KycStep { BASIC, PAN, AADHAAR, BIOMETRICS, SUBMITTED }

class KycViewModel(private val repository: KycRepository) : ViewModel() {

    private val _currentStep = mutableStateOf(KycStep.BASIC)
    val currentStep: State<KycStep> = _currentStep

    // UI State for the current operation
    private val _kycState = mutableStateOf<Resource<Unit>?>(null)
    val kycState: State<Resource<Unit>?> = _kycState

    // Form Data
    var applicationId = mutableStateOf("")
    var panNumber = mutableStateOf("")
    var panUri = mutableStateOf<Uri?>(null)
    var aadhaarNumber = mutableStateOf("")
    var aadhaarFrontUri = mutableStateOf<Uri?>(null)
    var aadhaarBackUri = mutableStateOf<Uri?>(null)
    var selfieUri = mutableStateOf<Uri?>(null)
    var signatureUri = mutableStateOf<Uri?>(null)

    fun moveToNextStep() {
        val nextIndex = _currentStep.value.ordinal + 1
        if (nextIndex < KycStep.entries.size) {
            _currentStep.value = KycStep.entries[nextIndex]
            _kycState.value = null // Clear state for the next step
        }
    }

    fun moveBackStep() {
        val currentIndex = _currentStep.value.ordinal
        if (currentIndex > 0) {
            _currentStep.value = KycStep.entries[currentIndex - 1]
            _kycState.value = null
        }
    }

    // --- 1. Init KYC (Updated to handle NetworkResponse) ---
    fun submitBasicDetails(name: String, email: String) {
        viewModelScope.launch {
            _kycState.value = Resource.Loading()
            when (val result = repository.initKyc(name, email)) {
                is NetworkResponse.Success -> {
                    val receivedId = result.body // NetworkResponse uses 'body'
                    if (receivedId.isNotEmpty()) {
                        applicationId.value = receivedId
                        _kycState.value = Resource.Success(Unit)
                        moveToNextStep()
                    } else {
                        _kycState.value = Resource.Error("Backend returned an empty ID")
                    }
                }
                is NetworkResponse.Error -> {
                    _kycState.value = Resource.Error(result.message) // NetworkResponse uses 'message'
                }
            }
        }
    }

    // --- 2. PAN Validation (Fixed currentAppId and Property Names) ---
    fun submitPan() {
        viewModelScope.launch {
            _kycState.value = Resource.Loading()

            val result = repository.validatePan(
                appId = applicationId.value, // Fixed from 'currentAppId'
                panNum = panNumber.value,
                panUri = panUri.value!!
            )

            when (result) {
                is NetworkResponse.Success -> {
                    // result.body is of type KycResponse
                    if (result.body.success) { // 'success' is a field in KycResponse
                        _kycState.value = Resource.Success(Unit)
                        moveToNextStep()
                    } else {
                        _kycState.value = Resource.Error(result.body.reason ?: "AI rejected document")
                    }
                }
                is NetworkResponse.Error -> {
                    _kycState.value = Resource.Error(result.message)
                }
            }
        }
    }

    // --- 3. Aadhaar Validation (Fixed Type Mismatch) ---
    fun submitAadhaar() {
        viewModelScope.launch {
            _kycState.value = Resource.Loading()
            val result = repository.validateAadhaar(
                appId = applicationId.value,
                aadhaarNum = aadhaarNumber.value,
                frontUri = aadhaarFrontUri.value!!,
                backUri = aadhaarBackUri.value!!
            )

            // Map NetworkResponse to Resource for UI
            when (result) {
                is NetworkResponse.Success -> {
                    _kycState.value = Resource.Success(Unit)
                    moveToNextStep()
                }
                is NetworkResponse.Error -> {
                    _kycState.value = Resource.Error(result.message)
                }
            }
        }
    }

    // --- 4. Final Submission ---
    fun submitFinalBiometrics() {
        viewModelScope.launch {
            _kycState.value = Resource.Loading()
            val result = repository.uploadBiometrics(
                appId = applicationId.value,
                photoUri = selfieUri.value!!,
                signUri = signatureUri.value!!
            )

            when (result) {
                is NetworkResponse.Success -> {
                    _kycState.value = Resource.Success(Unit)
                    _currentStep.value = KycStep.SUBMITTED
                }
                is NetworkResponse.Error -> {
                    _kycState.value = Resource.Error(result.message)
                }
            }
        }
    }
}