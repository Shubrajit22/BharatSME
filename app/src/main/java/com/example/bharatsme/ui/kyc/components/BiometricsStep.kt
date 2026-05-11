package com.example.bharatsme.ui.kyc.components

import androidx.compose.runtime.Composable
import com.example.bharatsme.ui.kyc.viewmodel.KycViewModel

@Composable
fun BiometricsStep(viewModel: KycViewModel) {
    BiometricsStepContent(
        selfieUri = viewModel.selfieUri.value,
        onSelfieUriChange = { viewModel.selfieUri.value = it },
        signatureUri = viewModel.signatureUri.value,
        onSignatureUriChange = { viewModel.signatureUri.value = it },
        state = viewModel.kycState.value,
        onNext = { viewModel.moveToNextStep() }
    )
}