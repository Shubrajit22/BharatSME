package com.example.bharatsme.ui.kyc.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.bharatsme.util.Resource

@Composable
fun BasicDetailsStepContent(
    userType: String,
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
            label = { Text(if (userType == "SME") "Authorized Signatory Name" else "Full Name (as per PAN)") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.Badge, null) }
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text(if (userType == "SME") "Official Business Email" else "Personal Email Address") },
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