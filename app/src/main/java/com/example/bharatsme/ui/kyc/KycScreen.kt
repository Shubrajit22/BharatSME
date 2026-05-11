package com.example.bharatsme.ui.kyc

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.bharatsme.ui.theme.BharatSMETheme
import com.example.bharatsme.util.Resource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KycScreen(
    viewModel: KycViewModel,
    onNavigateBack: () -> Unit,
    onComplete: () -> Unit
) {
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
    val progress = (step.ordinal + 1).toFloat() / KycStep.entries.size.toFloat()

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
                    KycStep.AADHAAR -> AadhaarUploadStepContent(
                        number = aadhaarNumber,
                        onNumberChange = onAadhaarNumberChange,
                        frontUri = aadhaarFrontUri,
                        onFrontUriChange = onAadhaarFrontUriChange,
                        backUri = aadhaarBackUri,
                        onBackUriChange = onAadhaarBackUriChange,
                        state = kycState,
                        onSubmit = onSubmitAadhaar
                    )
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

@Composable
fun PanUploadStepContent(
    panNumber: String,
    onPanNumberChange: (String) -> Unit,
    panUri: Uri?,
    onPanUriChange: (Uri?) -> Unit,
    state: Resource<Unit>?,
    onSubmit: () -> Unit
) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        onPanUriChange(uri)
    }

    Column {
        Text("PAN Verification", style = MaterialTheme.typography.titleLarge)
        OutlinedTextField(
            value = panNumber,
            onValueChange = { if (it.length <= 10) onPanNumberChange(it.uppercase()) },
            label = { Text("Enter PAN Number") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { launcher.launch("image/*") },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
        ) {
            Icon(Icons.Default.UploadFile, contentDescription = null)
            Text(" Upload PAN Image", color = Color.Black)
        }


        panUri?.let {
            Text("File selected: ${it.lastPathSegment}", color = Color.DarkGray)
        }

        Spacer(modifier = Modifier.weight(1f))

        // Inside PanUploadStepContent
        if (state is Resource.Error) {
            Text(
                text = state.message ?: "Verification Failed",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Button(
            onClick = onSubmit,
            enabled = panNumber.length == 10 && panUri != null && state !is Resource.Loading,
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            if (state is Resource.Loading) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                    Spacer(Modifier.width(8.dp))
                    Text("AI is verifying...")
                }
            } else {
                Text("Verify & Continue")
            }
        }
    }
}

@Composable
fun BasicDetailsStep(viewModel: KycViewModel) {
    BasicDetailsStepContent(
        state = viewModel.kycState.value,
        onSubmit = { name, email -> viewModel.submitBasicDetails(name, email) }
    )
}

@Composable
fun BasicDetailsStepContent(
    state: Resource<Unit>?,
    onSubmit: (String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Personal Information", style = MaterialTheme.typography.titleLarge)

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Full Name (as per PAN)") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.Badge, null) }
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Business Email") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.Email, null) }
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = { onSubmit(name, email) },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            enabled = name.isNotBlank() && email.contains("@") && state !is Resource.Loading
        ) {
            if (state is Resource.Loading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            else Text("Initialize Application")
        }
    }
}

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

@Composable
fun AadhaarUploadStepContent(
    number: String,
    onNumberChange: (String) -> Unit,
    frontUri: Uri?,
    onFrontUriChange: (Uri?) -> Unit,
    backUri: Uri?,
    onBackUriChange: (Uri?) -> Unit,
    state: Resource<Unit>?,
    onSubmit: () -> Unit
) {
    val frontLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { onFrontUriChange(it) }
    val backLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { onBackUriChange(it) }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Aadhaar Verification", style = MaterialTheme.typography.titleLarge)

        OutlinedTextField(
            value = number,
            onValueChange = { if (it.length <= 12) onNumberChange(it.filter { char -> char.isDigit() }) },
            label = { Text("Aadhaar Number") },
            placeholder = { Text("XXXX XXXX XXXX") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            KycUploadCard(
                label = "Front Side",
                isDone = frontUri != null,
                modifier = Modifier.weight(1f)
            ) { frontLauncher.launch("image/*") }

            KycUploadCard(
                label = "Back Side",
                isDone = backUri != null,
                modifier = Modifier.weight(1f)
            ) { backLauncher.launch("image/*") }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onSubmit,
            modifier = Modifier.fillMaxWidth(),
            enabled = number.length == 12 && frontUri != null && backUri != null && state !is Resource.Loading
        ) {
            if (state is Resource.Loading) CircularProgressIndicator(modifier = Modifier.size(24.dp))
            else Text("Verify Aadhaar")
        }
    }
}

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

@Composable
fun BiometricsStepContent(
    selfieUri: Uri?,
    onSelfieUriChange: (Uri?) -> Unit,
    signatureUri: Uri?,
    onSignatureUriChange: (Uri?) -> Unit,
    state: Resource<Unit>?, // Pass the state here
    onNext: () -> Unit
) {
    val selfieLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { onSelfieUriChange(it) }
    val signatureLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { onSignatureUriChange(it) }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Biometric Identity", style = MaterialTheme.typography.titleLarge)

        Text(
            "Our AI will match your selfie against your ID documents and check for existing accounts.",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )

        KycUploadCard(
            label = "Capture Live Selfie",
            isDone = selfieUri != null,
            isAnalyzing = state is Resource.Loading, // Show loading on the card
            modifier = Modifier.fillMaxWidth()
        ) { selfieLauncher.launch("image/*") }

        KycUploadCard(
            label = "Upload Signature",
            isDone = signatureUri != null,
            modifier = Modifier.fillMaxWidth()
        ) { signatureLauncher.launch("image/*") }

        if (state is Resource.Error) {
            // This is where "Duplicate Account Found" or "Liveness Failed" appears
            Text(state.message!!, color = Color.Red, style = MaterialTheme.typography.bodyMedium)
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onNext,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            enabled = selfieUri != null && signatureUri != null && state !is Resource.Loading
        ) {
            if (state is Resource.Loading) Text("Running Fraud Detection...")
            else Text("Complete Onboarding")
        }
    }
}

@Composable
fun SuccessStep(onComplete: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            modifier = Modifier.size(100.dp),
            tint = Color(0xFF4CAF50)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text("KYC Completed!", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text("Your details are being reviewed.", color = Color.Gray)

        Spacer(modifier = Modifier.height(48.dp))

        Button(onClick = onComplete, modifier = Modifier.fillMaxWidth()) {
            Text("Back to Dashboard")
        }
    }
}

@Composable
fun KycUploadCard(
    label: String,
    isDone: Boolean,
    isAnalyzing: Boolean = false, // New state
    modifier: Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.height(100.dp).clickable(enabled = !isAnalyzing) { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = when {
                isAnalyzing -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                isDone -> Color(0xFFE8F5E9)
                else -> MaterialTheme.colorScheme.surfaceVariant
            }
        ),
        border = if (isDone) BorderStroke(1.dp, Color(0xFF4CAF50)) else null
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                if (isAnalyzing) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                } else {
                    Icon(
                        if (isDone) Icons.Default.Check else Icons.Default.CloudUpload,
                        contentDescription = null,
                        tint = if (isDone) Color(0xFF2E7D32) else MaterialTheme.colorScheme.primary
                    )
                }
                Text(
                    text = if (isAnalyzing) "Analyzing..." else label,
                    style = MaterialTheme.typography.labelMedium
                )
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
            onMoveToNextStep = {}
        )
    }
}