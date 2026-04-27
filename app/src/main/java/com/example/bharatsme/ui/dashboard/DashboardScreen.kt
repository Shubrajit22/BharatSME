package com.example.bharatsme.ui.dashboard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FactCheck
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AddBusiness
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bharatsme.ui.theme.BharatSMETheme
import com.example.bharatsme.data.remote.dto.LoanResponse
import com.example.bharatsme.util.Resource
import kotlin.collections.emptyList

enum class DashboardTab { HOME, SETTINGS }

@Composable
fun DashboardScreen(
    userName: String,
    viewModel: DashboardViewModel,
    onNavigateToKyc: () -> Unit,
    onNavigateToNewLoan: () -> Unit,
    onLogout: () -> Unit
) {
    // Explicitly use the resourceState from the ViewModel
    val resourceState by viewModel.uiState

    LaunchedEffect(Unit) {
        viewModel.loadDashboardData()
    }

    DashboardScreenContent(
        userName = userName,
        onEvaluateClick = { viewModel.evaluateLoan(it) },
        resourceState = resourceState,
        onNavigateToKyc = onNavigateToKyc,
        onNavigateToNewLoan = onNavigateToNewLoan,
        onLogout = onLogout
    )
}

@Composable
fun DashboardScreenContent(
    userName: String,
    onEvaluateClick: (String) -> Unit,
    resourceState: Resource<List<LoanResponse>>,
    onNavigateToKyc: () -> Unit,
    onNavigateToNewLoan: () -> Unit,
    onLogout: () -> Unit
) {
    // Local state to track which tab is active
    var selectedTab by remember { mutableStateOf(DashboardTab.HOME) }

    Scaffold(
        topBar = {
            // TopBar remains visible for both tabs for consistency
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = if (selectedTab == DashboardTab.HOME) "Hello, $userName" else "Settings",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                if (selectedTab == DashboardTab.HOME) {
                    Text("Welcome to BharatSME Portal", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                }
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = selectedTab == DashboardTab.HOME,
                    onClick = { selectedTab = DashboardTab.HOME },
                    label = { Text("Dashboard") },
                    icon = { Icon(Icons.Default.Dashboard, contentDescription = null) }
                )
                NavigationBarItem(
                    selected = selectedTab == DashboardTab.SETTINGS,
                    onClick = { selectedTab = DashboardTab.SETTINGS },
                    label = { Text("Settings") },
                    icon = { Icon(Icons.Default.Settings, contentDescription = null) }
                )
            }
        }
    ) { padding ->
        // Switch content based on selected tab
        Column(modifier = Modifier.padding(padding)) {
            when (selectedTab) {
                DashboardTab.HOME -> {
                    DashboardGridView(
                        resourceState = resourceState,
                        onEvaluateClick = onEvaluateClick,
                        onNavigateToKyc = onNavigateToKyc,
                        onNavigateToNewLoan = onNavigateToNewLoan
                    )
                }
                DashboardTab.SETTINGS -> {
                    SettingsView(onLogout = onLogout)
                }
            }
        }
    }
}

@Composable
fun DashboardGridView(
    resourceState: Resource<List<LoanResponse>>,
    onEvaluateClick: (String) -> Unit,
    onNavigateToKyc: () -> Unit,
    onNavigateToNewLoan: () -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize()
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

        when (resourceState) {
            is Resource.Loading -> {
                item(span = { GridItemSpan(2) }) {
                    Box(Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            }
            is Resource.Success -> {
                val apps = resourceState.data ?: emptyList()
                if (apps.isEmpty()) {
                    item(span = { GridItemSpan(2) }) {
                        EmptyApplicationsView()
                    }
                } else {
                    items(apps) { app ->
                        LoanApplicationCard(app = app, onEvaluateClick = onEvaluateClick)
                    }
                }
            }
            is Resource.Error -> {
                item(span = { GridItemSpan(2) }) {
                    Text("Error: ${resourceState.message}", color = Color.Red)
                }
            }
        }
    }
}

@Composable
fun SettingsView(onLogout: () -> Unit) {
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
            modifier = Modifier.clickable { }
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
            onNavigateToNewLoan = {},
            onEvaluateClick = {},
            onLogout = {}
        )
    }
}