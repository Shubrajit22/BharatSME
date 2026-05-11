package com.example.bharatsme.ui.dashboard.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.bharatsme.data.remote.dto.UserProfile
import com.example.bharatsme.util.Resource

@Composable
fun ProfileView(
    state: Resource<UserProfile>,
    onRetry: () -> Unit
) {
    when (state) {
        is Resource.Loading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is Resource.Error -> {
            Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                Text("Failed to load profile", color = Color.Red)
                Button(onClick = onRetry) { Text("Retry") }
            }
        }
        is Resource.Success -> {
            val profile = state.data!!
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 1. Account Header
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier.size(60.dp),
                            shape = RoundedCornerShape(30.dp),
                            color = MaterialTheme.colorScheme.primary
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    profile.fullName.take(1).uppercase(),
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = Color.White
                                )
                            }
                        }
                        Spacer(Modifier.width(16.dp))
                        Column {
                            Text(profile.fullName, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                            Text(profile.username, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                        }
                    }
                }

                // 2. Basic Details Section
                Text("Basic Details", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(8.dp)) {
                        ProfileInfoRow(label = "Email", value = profile.email, icon = Icons.Default.Inbox)
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                        ProfileInfoRow(label = "Account Type", value = profile.userType, icon = Icons.Default.Person)
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                        ProfileInfoRow(label = "Member Since", value = profile.createdAt.split("T")[0], icon = Icons.Default.Dashboard)
                    }
                }

                // 3. KYC Status Section
                Text("Verification Status", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                KycStatusCard(status = profile.kycStatus)
            }
        }
    }
}