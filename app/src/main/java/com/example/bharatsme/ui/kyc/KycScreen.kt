package com.example.bharatsme.ui.kyc

import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.bharatsme.ui.kyc.components.AadhaarUploadStepContent
import com.example.bharatsme.ui.kyc.components.BasicDetailsStepContent
import com.example.bharatsme.ui.kyc.components.BiometricsStepContent
import com.example.bharatsme.ui.kyc.components.PanUploadStepContent
import com.example.bharatsme.ui.kyc.components.SuccessStep
import com.example.bharatsme.ui.kyc.viewmodel.KycStep
import com.example.bharatsme.ui.kyc.viewmodel.KycViewModel
import com.example.bharatsme.ui.theme.BharatSMETheme
import com.example.bharatsme.util.Resource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KycScreen(
    viewModel: KycViewModel,
    onNavigateBack: () -> Unit,
    onComplete: () -> Unit
) {
    val userType by viewModel.userType

    val step by viewModel.currentStep
    val kycState by viewModel.kycState
    val panNumber by viewModel.panNumber
    val panUri by viewModel.panUri
    val aadhaarNumber by viewModel.aadhaarNumber
    val aadhaarFrontUri by viewModel.aadhaarFrontUri
    val aadhaarBackUri by viewModel.aadhaarBackUri
    val selfieUri by viewModel.selfieUri
    val signatureUri by viewModel.signatureUri

    KycScreenContent(
        userType = userType,
        step = step,
        kycState = kycState,
        panNumber = panNumber,
        onPanNumberChange = { viewModel.panNumber.value = it },
        panUri = panUri,
        onPanUriChange = { viewModel.panUri.value = it },
        aadhaarNumber = aadhaarNumber,
        onAadhaarNumberChange = { viewModel.aadhaarNumber.value = it },
        aadhaarFrontUri = aadhaarFrontUri,
        onAadhaarFrontUriChange = { viewModel.aadhaarFrontUri.value = it },
        aadhaarBackUri = aadhaarBackUri,
        onAadhaarBackUriChange = { viewModel.aadhaarBackUri.value = it },
        selfieUri = selfieUri,
        onSelfieUriChange = { viewModel.selfieUri.value = it },
        signatureUri = signatureUri,
        onSignatureUriChange = { viewModel.signatureUri.value = it },
        onNavigateBack = onNavigateBack,
        onMoveBackStep = { viewModel.moveBackStep() },
        onComplete = onComplete,
        onSubmitBasicDetails = { name, email -> viewModel.submitBasicDetails(name, email) },
        onSubmitPan = { viewModel.submitPan() },
        onSubmitAadhaar = { viewModel.submitAadhaar() },
        onMoveToNextStep = { viewModel.moveToNextStep() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KycScreenContent(
    userType: String,
    step: KycStep,
    kycState: Resource<Unit>?,
    panNumber: String,
    onPanNumberChange: (String) -> Unit,
    panUri: Uri?,
    onPanUriChange: (Uri?) -> Unit,
    aadhaarNumber: String,
    onAadhaarNumberChange: (String) -> Unit,
    aadhaarFrontUri: Uri?,
    onAadhaarFrontUriChange: (Uri?) -> Unit,
    aadhaarBackUri: Uri?,
    onAadhaarBackUriChange: (Uri?) -> Unit,
    selfieUri: Uri?,
    onSelfieUriChange: (Uri?) -> Unit,
    signatureUri: Uri?,
    onSignatureUriChange: (Uri?) -> Unit,
    onNavigateBack: () -> Unit,
    onMoveBackStep: () -> Unit,
    onComplete: () -> Unit,
    onSubmitBasicDetails: (String, String) -> Unit,
    onSubmitPan: () -> Unit,
    onSubmitAadhaar: () -> Unit,
    onMoveToNextStep: () -> Unit
) {
    val totalSteps = if (userType == "SME") 4 else 5
    val currentStepIndex = when {
        userType == "SME" && step.ordinal > KycStep.PAN.ordinal -> step.ordinal // Adjusted for skip
        else -> step.ordinal + 1
    }
    val progress = currentStepIndex.toFloat() / totalSteps.toFloat()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("KYC Verification", style = MaterialTheme.typography.titleMedium)
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (step == KycStep.BASIC) {
                            onNavigateBack()
                        } else {
                            onMoveBackStep()
                        }
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp, vertical = 8.dp)
        ) {
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(CircleShape),
            )

            Spacer(modifier = Modifier.height(24.dp))

            Box(modifier = Modifier.weight(1f)) {
                when (step) {
                    KycStep.BASIC -> BasicDetailsStepContent(
                        userType = userType, // Pass down
                        state = kycState,
                        onSubmit = onSubmitBasicDetails
                    )
                    KycStep.PAN -> PanUploadStepContent(
                        panNumber = panNumber,
                        onPanNumberChange = onPanNumberChange,
                        panUri = panUri,
                        onPanUriChange = onPanUriChange,
                        state = kycState,
                        onSubmit = onSubmitPan
                    )
                    KycStep.AADHAAR ->
                        if(userType == "INDIVIDUAL"){
                            AadhaarUploadStepContent(
                                number = aadhaarNumber,
                                onNumberChange = onAadhaarNumberChange,
                                frontUri = aadhaarFrontUri,
                                onFrontUriChange = onAadhaarFrontUriChange,
                                backUri = aadhaarBackUri,
                                onBackUriChange = onAadhaarBackUriChange,
                                state = kycState,
                                onSubmit = onSubmitAadhaar
                            )
                        }

                    KycStep.BIOMETRICS -> BiometricsStepContent(
                        selfieUri = selfieUri,
                        onSelfieUriChange = onSelfieUriChange,
                        signatureUri = signatureUri,
                        onSignatureUriChange = onSignatureUriChange,
                        state = kycState,
                        onNext = onMoveToNextStep
                    )
                    KycStep.SUBMITTED -> SuccessStep(onComplete)
                }
            }
        }
    }
}
@Preview(showBackground = true)
@Composable
fun KycScreenPreview() {
    BharatSMETheme {
        KycScreenContent(
            step = KycStep.BASIC,
            kycState = null,
            panNumber = "",
            onPanNumberChange = {},
            panUri = null,
            onPanUriChange = {},
            aadhaarNumber = "",
            onAadhaarNumberChange = {},
            aadhaarFrontUri = null,
            onAadhaarFrontUriChange = {},
            aadhaarBackUri = null,
            onAadhaarBackUriChange = {},
            selfieUri = null,
            onSelfieUriChange = {},
            signatureUri = null,
            onSignatureUriChange = {},
            onNavigateBack = {},
            onMoveBackStep = {},
            onComplete = {},
            onSubmitBasicDetails = { _, _ -> },
            onSubmitPan = {},
            onSubmitAadhaar = {},
            onMoveToNextStep = {},
            userType = "INDIVIDUAL"
        )
    }
}