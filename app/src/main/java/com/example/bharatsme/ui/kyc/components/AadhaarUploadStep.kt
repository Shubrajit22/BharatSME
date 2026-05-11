package com.example.bharatsme.ui.kyc.components

import androidx.compose.runtime.Composable
import com.example.bharatsme.ui.kyc.viewmodel.KycViewModel

@Composable
fun AadhaarUploadStep(viewModel: KycViewModel) {
    AadhaarUploadStepContent(
        number = viewModel.aadhaarNumber.value,
        onNumberChange = { viewModel.aadhaarNumber.value = it },
        frontUri = viewModel.aadhaarFrontUri.value,
        onFrontUriChange = { viewModel.aadhaarFrontUri.value = it },
        backUri = viewModel.aadhaarBackUri.value,
        onBackUriChange = { viewModel.aadhaarBackUri.value = it },
        state = viewModel.kycState.value,
        onSubmit = { viewModel.submitAadhaar() }
    )
}