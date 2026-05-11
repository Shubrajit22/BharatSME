package com.example.bharatsme.ui.dashboard.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun SettingsView(onProfileClick: () -> Unit,onLogout: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Simple placeholder settings list
        ListItem(
            headlineContent = { Text("Profile Settings") },
            supportingContent = { Text("Edit your personal and business details") },
            leadingContent = { Icon(Icons.Default.Person, contentDescription = null) },
            modifier = Modifier.clickable { onProfileClick() }
        )
        HorizontalDivider()
        ListItem(
            headlineContent = { Text("Security") },
            supportingContent = { Text("Manage passwords and biometrics") },
            leadingContent = { Icon(Icons.Default.Lock, contentDescription = null) },
            modifier = Modifier.clickable { }
        )
        HorizontalDivider()
        ListItem(
            headlineContent = { Text("Logout", color = Color.Red) },
            leadingContent = { Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, tint = Color.Red) },
            modifier = Modifier.clickable { onLogout() }
        )
    }
}