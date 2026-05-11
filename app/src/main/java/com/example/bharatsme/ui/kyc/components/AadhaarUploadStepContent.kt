package com.example.bharatsme.ui.kyc.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.bharatsme.util.Resource

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