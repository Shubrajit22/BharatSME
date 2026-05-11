package com.example.bharatsme.ui.dashboard.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun ProfileInfoRow(label: String, value: String, icon: ImageVector) {
    ListItem(
        headlineContent = { Text(label, style = MaterialTheme.typography.labelMedium, color = Color.Gray) },
        supportingContent = { Text(value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold) },
        leadingContent = { Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp)) }
    )
}