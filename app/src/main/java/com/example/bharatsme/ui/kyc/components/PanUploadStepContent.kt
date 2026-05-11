package com.example.bharatsme.ui.kyc.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.bharatsme.util.Resource

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