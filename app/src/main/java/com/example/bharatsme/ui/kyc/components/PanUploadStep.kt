package com.example.bharatsme.ui.kyc.components

import androidx.compose.runtime.Composable
import com.example.bharatsme.ui.kyc.viewmodel.KycViewModel

@Composable
fun PanUploadStep(viewModel: KycViewModel) {
    PanUploadStepContent(
        panNumber = viewModel.panNumber.value,
        onPanNumberChange = { viewModel.panNumber.value = it },
        panUri = viewModel.panUri.value,
        onPanUriChange = { viewModel.panUri.value = it },
        state = viewModel.kycState.value,
        onSubmit = { viewModel.submitPan() }
    )
}