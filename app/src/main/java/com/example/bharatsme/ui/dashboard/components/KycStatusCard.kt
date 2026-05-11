package com.example.bharatsme.ui.dashboard.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FactCheck
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun KycStatusCard(status: String) {
    val (color, icon, description) = when (status) {
        "VERIFIED" -> Triple(Color(0xFF2E7D32), Icons.AutoMirrored.Filled.FactCheck, "Your identity has been verified. You can now apply for higher loan limits.")
        "PENDING" -> Triple(Color(0xFFE65100), Icons.Default.Settings, "Your documents are currently under review by our AI system.")
        else -> Triple(Color(0xFFC62828), Icons.Default.Lock, "Verification failed or not started. Please complete your KYC to unlock features.")
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.5f))
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(32.dp))
            Spacer(Modifier.width(16.dp))
            Column {
                Text(status, fontWeight = FontWeight.ExtraBold, color = color)
                Text(description, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}