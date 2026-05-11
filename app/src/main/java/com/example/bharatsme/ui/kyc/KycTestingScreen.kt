package com.example.bharatsme.ui.kyc

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bharatsme.data.remote.dto.KycResponse
import com.example.bharatsme.ui.kyc.viewmodel.VerificationUiState
import com.example.bharatsme.ui.kyc.viewmodel.VerificationViewModel
import com.example.bharatsme.ui.theme.BharatSMETheme

@Composable
fun KycTestingScreen(viewModel: VerificationViewModel = viewModel()) {
    KycTestingContent(
        uiState = viewModel.uiState,
        onCaptureClick = { appId, fullName, idNumber ->
            /* Trigger CameraX and then call viewModel.submitOnboarding */
        }
    )
}

@Composable
private fun KycTestingContent(
    uiState: VerificationUiState,
    onCaptureClick: (String, String, String) -> Unit
) {
    var appId by remember { mutableStateOf("SME-12345") }
    var fullName by remember { mutableStateOf("Midanka Lahon") }
    var idNumber by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("AI KYC Testing Portal", style = MaterialTheme.typography.headlineMedium)

        OutlinedTextField(value = appId, onValueChange = { appId = it }, label = { Text("App ID") })
        OutlinedTextField(value = fullName, onValueChange = { fullName = it }, label = { Text("Full Name") })
        OutlinedTextField(value = idNumber, onValueChange = { idNumber = it }, label = { Text("ID Number (PAN/Aadhaar)") })

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = { onCaptureClick(appId, fullName, idNumber) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Capture & Verify")
        }

        when (val state = uiState) {
            is VerificationUiState.Loading -> CircularProgressIndicator()
            is VerificationUiState.Success -> {
                Card(colors = CardDefaults.cardColors(containerColor = Color.Green.copy(alpha = 0.1f))) {
                    Text("Status: ${state.data.status}", modifier = Modifier.padding(8.dp))
                    Text("Detected ID: ${state.data.extractedId}", modifier = Modifier.padding(8.dp))
                }
            }
            is VerificationUiState.Error -> {
                Text("Error: ${state.message}", color = Color.Red)
            }
            else -> {}
        }
    }
}

@Preview(showBackground = true, name = "Idle State")
@Composable
fun KycTestingScreenIdlePreview() {
    BharatSMETheme {
        KycTestingContent(
            uiState = VerificationUiState.Idle,
            onCaptureClick = { _, _, _ -> }
        )
    }
}

@Preview(showBackground = true, name = "Loading State")
@Composable
fun KycTestingScreenLoadingPreview() {
    BharatSMETheme {
        KycTestingContent(
            uiState = VerificationUiState.Loading,
            onCaptureClick = { _, _, _ -> }
        )
    }
}

@Preview(showBackground = true, name = "Success State")
@Composable
fun KycTestingScreenSuccessPreview() {
    BharatSMETheme {
        KycTestingContent(
            uiState = VerificationUiState.Success(
                data = KycResponse(
                    status = "VERIFIED",
                    extractedId = "XXXX-XXXX-1234",
                    success = true,
                    reason = "",
                    fraudFlags = listOf("false"),
                )
            ),
            onCaptureClick = { _, _, _ -> }
        )
    }
}

@Preview(showBackground = true, name = "Error State")
@Composable
fun KycTestingScreenErrorPreview() {
    BharatSMETheme {
        KycTestingContent(
            uiState = VerificationUiState.Error("Failed to connect to server"),
            onCaptureClick = { _, _, _ -> }
        )
    }
}
