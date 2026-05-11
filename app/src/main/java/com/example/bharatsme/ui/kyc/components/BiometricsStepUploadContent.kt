package com.example.bharatsme.ui.kyc.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.bharatsme.util.Resource

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