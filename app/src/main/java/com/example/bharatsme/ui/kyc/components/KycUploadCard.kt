package com.example.bharatsme.ui.kyc.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

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