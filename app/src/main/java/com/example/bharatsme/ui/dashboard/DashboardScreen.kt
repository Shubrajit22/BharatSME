package com.example.bharatsme.ui.dashboard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FactCheck
import androidx.compose.material.icons.filled.AddBusiness
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.example.bharatsme.ui.theme.BharatSMETheme
import com.example.bharatsme.data.remote.dto.LoanResponse
import com.example.bharatsme.util.Resource
import kotlin.collections.emptyList

@Composable
fun DashboardScreen(
    userName: String,
    viewModel: DashboardViewModel,
    onNavigateToKyc: () -> Unit,
    onNavigateToNewLoan: () -> Unit
) {
    // Explicitly use the resourceState from the ViewModel
    val resourceState by viewModel.uiState

    LaunchedEffect(Unit) {
        viewModel.loadDashboardData()
    }

    DashboardScreenContent(
        userName = userName,
        resourceState = resourceState,
        onNavigateToKyc = onNavigateToKyc,
        onNavigateToNewLoan = onNavigateToNewLoan
    )
}

@Composable
fun DashboardScreenContent(
    userName: String,
    resourceState: Resource<List<LoanResponse>>,
    onNavigateToKyc: () -> Unit,
    onNavigateToNewLoan: () -> Unit
) {
    Scaffold(
        topBar = {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Hello, $userName", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Text("Welcome to BharatSME Portal", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
            }
        }
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(padding).fillMaxSize()
        ) {
            item(span = { GridItemSpan(2) }) {
                Text("Quick Actions", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            }

            item {
                DashboardActionCard(
                    title = "Complete KYC",
                    subtitle = "Verify Identity",
                    icon = Icons.AutoMirrored.Filled.FactCheck,
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    onClick = onNavigateToKyc
                )
            }

            item {
                DashboardActionCard(
                    title = "New Loan",
                    subtitle = "Apply in Minutes",
                    icon = Icons.Default.AddBusiness,
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    onClick = onNavigateToNewLoan
                )
            }

            item(span = { GridItemSpan(2) }) {
                Spacer(modifier = Modifier.height(16.dp))
                Text("Your Applications", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            }

            // Use 'current' to allow Kotlin to smart-cast the Resource type
            when (val current = resourceState) {
                is Resource.Loading -> {
                    item(span = { GridItemSpan(2) }) {
                        Box(Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                }
                // REMOVE the <*> here. Kotlin knows current is Resource.Success<List<LoanResponse>>
                is Resource.Success -> {
                    // 1. current.data is now correctly seen as List<LoanResponse>?
                    // 2. Use parentheses for isEmpty()
                    val apps = current.data ?: emptyList()

                    if (apps.isEmpty()) {
                        item(span = { GridItemSpan(2) }) {
                            EmptyApplicationsView()
                        }
                    } else {
                        // 3. apps is now recognized as a List, so this items() call will work
                        items(apps) { app ->
                            LoanApplicationCard(app)
                        }
                    }
                }
                is Resource.Error -> {
                    item(span = { GridItemSpan(2) }) {
                        Text("Error: ${current.message}", color = Color.Red)
                    }
                }
            }
        }
    }
}

@Composable
fun DashboardActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    containerColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().height(140.dp).clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = containerColor),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp).fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(32.dp))
            Column {
                Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Text(subtitle, style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

@Composable
fun LoanApplicationCard(app: LoanResponse) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text("₹${app.requestedLoanAmount}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(app.businessType, style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(8.dp))

            // Status Chip
            Surface(
                color = if (app.eligibilityStatus == "ELIGIBLE") Color(0xFFE8F5E9) else Color(0xFFFFF3E0),
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(
                    text = app.eligibilityStatus,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = if (app.eligibilityStatus == "ELIGIBLE") Color(0xFF2E7D32) else Color(0xFFE65100)
                )
            }
        }
    }
}

@Composable
fun EmptyApplicationsView() {
    Column(
        modifier = Modifier.fillMaxWidth().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(Icons.Default.Inbox, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color.LightGray)
        Text("No applications found", color = Color.Gray)
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardScreenPreview() {
    val sampleLoans = listOf(
        LoanResponse(
            id = "1",
            applicantName = "John Doe",
            businessType = "Retail",
            turnoverBand = "1M-5M",
            requestedLoanAmount = "500000",
            yearsInBusiness = 3,
            eligibilityStatus = "ELIGIBLE",
            preScreenResult = "PASS",
            lockedByStaff = false,
            createdAt = "2023-01-01",
            updatedAt = "2023-01-01"
        ),
        LoanResponse(
            id = "2",
            applicantName = "Jane Smith",
            businessType = "Manufacturing",
            turnoverBand = "5M-10M",
            requestedLoanAmount = "1500000",
            yearsInBusiness = 5,
            eligibilityStatus = "PENDING",
            preScreenResult = "PASS",
            lockedByStaff = false,
            createdAt = "2023-01-02",
            updatedAt = "2023-01-02"
        )
    )

    BharatSMETheme {
        DashboardScreenContent(
            userName = "Abhishek",
            resourceState = Resource.Success(sampleLoans),
            onNavigateToKyc = {},
            onNavigateToNewLoan = {}
        )
    }
}