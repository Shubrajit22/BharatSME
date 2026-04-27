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
import androidx.compose.ui.unit.dp
import com.example.bharatsme.util.Resource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KycScreen(
    viewModel: KycViewModel,
    onNavigateBack: () -> Unit, // Add this to handle going back to Dashboard
    onComplete: () -> Unit
) {
    val step by viewModel.currentStep
    val progress = (step.ordinal + 1).toFloat() / KycStep.entries.size.toFloat()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("KYC Verification", style = MaterialTheme.typography.titleMedium)
                },
                navigationIcon = {
                    IconButton(onClick = {
                        // If on the first step, go back to Dashboard. Otherwise, go back one step.
                        if (step == KycStep.BASIC) {
                            onNavigateBack()
                        } else {
                            viewModel.moveBackStep()
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
                    scrolledContainerColor = Color.Unspecified,
                    navigationIconContentColor = Color.Unspecified,
                    titleContentColor = Color.Unspecified,
                    actionIconContentColor = Color.Unspecified
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // Use Scaffold padding
                .padding(horizontal = 24.dp, vertical = 8.dp)
        ) {
            // Progress Indicator shifted below the TopBar
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
                    KycStep.BASIC -> BasicDetailsStep(viewModel)
                    KycStep.PAN -> PanUploadStep(viewModel)
                    KycStep.AADHAAR -> AadhaarUploadStep(viewModel)
                    KycStep.BIOMETRICS -> BiometricsStep(viewModel)
                    KycStep.SUBMITTED -> SuccessStep(onComplete)
                }
            }
        }
    }
}

@Composable
fun PanUploadStep(viewModel: KycViewModel) {
    var panNumber by viewModel.panNumber
    var selectedImageUri by viewModel.panUri

    val state by viewModel.kycState

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

    Column {
        Text("PAN Verification", style = MaterialTheme.typography.titleLarge)
        OutlinedTextField(
            value = panNumber,
            onValueChange = { if (it.length <= 10) panNumber = it.uppercase() },
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

        selectedImageUri?.let {
            Text("File selected: ${it.lastPathSegment}", color = Color.DarkGray)
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = { viewModel.submitPan() }, // Trigger network call
            enabled = panNumber.length == 10 && selectedImageUri != null && state !is Resource.Loading,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (state is Resource.Loading) CircularProgressIndicator(modifier = Modifier.size(24.dp))
            else Text("Verify PAN")
        }
    }
}

@Composable
fun BasicDetailsStep(viewModel: KycViewModel) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    val state by viewModel.kycState

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
            onClick = { viewModel.submitBasicDetails(name, email) },
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
    var number by viewModel.aadhaarNumber
    var frontUri by viewModel.aadhaarFrontUri
    var backUri by viewModel.aadhaarBackUri

    val state by viewModel.kycState

    val frontLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { frontUri = it }
    val backLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { backUri = it }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Aadhaar Verification", style = MaterialTheme.typography.titleLarge)

        OutlinedTextField(
            value = number,
            onValueChange = { if (it.length <= 12) number = it.filter { char -> char.isDigit() } },
            label = { Text("Aadhaar Number") },
            placeholder = { Text("XXXX XXXX XXXX") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            KycUploadCard("Front Side", frontUri != null, Modifier.weight(1f)) { frontLauncher.launch("image/*") }
            KycUploadCard("Back Side", backUri != null, Modifier.weight(1f)) { backLauncher.launch("image/*") }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = { viewModel.submitAadhaar() }, // Trigger network call
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
    var selfieUri by viewModel.selfieUri
    var signatureUri by viewModel.signatureUri

    val selfieLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { selfieUri = it }
    val signatureLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { signatureUri = it }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Final Biometrics", style = MaterialTheme.typography.titleLarge)
        Text("Please upload a clear selfie and a photo of your signature on white paper.", style = MaterialTheme.typography.bodySmall)

        KycUploadCard("Capture Selfie", selfieUri != null, Modifier.fillMaxWidth()) { selfieLauncher.launch("image/*") }
        KycUploadCard("Upload Signature", signatureUri != null, Modifier.fillMaxWidth()) { signatureLauncher.launch("image/*") }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = { viewModel.moveToNextStep() }, // In a real app, call viewModel.submitFinalKyc()
            modifier = Modifier.fillMaxWidth(),
            enabled = selfieUri != null && signatureUri != null
        ) {
            Text("Final Submission")
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
fun KycUploadCard(label: String, isDone: Boolean, modifier: Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier.height(100.dp).clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isDone) Color(0xFFE8F5E9) else MaterialTheme.colorScheme.surfaceVariant
        ),
        border = if (isDone) BorderStroke(1.dp, Color(0xFF4CAF50)) else null
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    if (isDone) Icons.Default.Check else Icons.Default.CloudUpload,
                    contentDescription = null,
                    tint = if (isDone) Color(0xFF2E7D32) else MaterialTheme.colorScheme.primary
                )
                Text(label, style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}