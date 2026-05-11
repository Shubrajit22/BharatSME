package com.example.bharatsme.ui.dashboard.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.bharatsme.data.remote.dto.LoanResponse

@Composable
fun LoanApplicationCard(
    app: LoanResponse,
    onEvaluateClick: (String) -> Unit // New callback
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("₹${app.requestedLoanAmount}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(app.businessType, style = MaterialTheme.typography.bodySmall)
                }

                // If the result is still 'PENDING', show a button to evaluate
                if (app.preScreenResult == "PENDING") {
                    TextButton(onClick = { onEvaluateClick(app.id) }) {
                        Text("Evaluate", style = MaterialTheme.typography.labelMedium)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Status Chip
            Surface(
                color = when(app.preScreenResult) {
                    "ELIGIBLE" -> Color(0xFFE8F5E9)
                    "REJECTED" -> Color(0xFFFFEBEE)
                    else -> Color(0xFFFFF3E0)
                },
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(
                    text = app.preScreenResult,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = when(app.preScreenResult) {
                        "ELIGIBLE" -> Color(0xFF2E7D32)
                        "REJECTED" -> Color(0xFFC62828)
                        else -> Color(0xFFE65100)
                    }
                )
            }
        }
    }
}