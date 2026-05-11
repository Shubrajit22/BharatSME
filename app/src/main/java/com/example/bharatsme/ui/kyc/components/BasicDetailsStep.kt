package com.example.bharatsme.ui.kyc.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.example.bharatsme.ui.kyc.viewmodel.KycViewModel

@Composable
fun BasicDetailsStep(viewModel: KycViewModel) {
    val userType by viewModel.userType // Extract the state

    BasicDetailsStepContent(
        userType = userType, // Pass it here
        state = viewModel.kycState.value,
        onSubmit = { name, email -> viewModel.submitBasicDetails(name, email) }
    )
}