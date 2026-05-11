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
import com.example.bharatsme.ui.theme.BharatSMETheme
import com.example.bharatsme.data.remote.dto.LoanResponse
import com.example.bharatsme.data.remote.dto.UserProfile
import com.example.bharatsme.util.Resource
import kotlin.collections.emptyList

enum class DashboardTab { HOME,PROFILE, SETTINGS }

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
    val profileState by viewModel.profileState

    LaunchedEffect(Unit) {
        viewModel.loadDashboardData()
        viewModel.loadProfile()
    }

    DashboardScreenContent(
        userName = userName,
        onEvaluateClick = { viewModel.evaluateLoan(it) },
        resourceState = resourceState,
        profileState = profileState,      // Pass it down
        onLoadProfile = { viewModel.loadProfile() }, // Pass the action
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
    profileState: Resource<UserProfile>, // Add this
    onLoadProfile: () -> Unit,
    onNavigateToKyc: () -> Unit,
    onNavigateToNewLoan: () -> Unit,
    onLogout: () -> Unit
) {
    // Local state to track which tab is active
    var selectedTab by remember { mutableStateOf(DashboardTab.HOME) }

    Scaffold(
        topBar = {
            val title = when(selectedTab) {
                DashboardTab.HOME -> "Hello, $userName"
                DashboardTab.PROFILE -> "My Profile"
                DashboardTab.SETTINGS -> "Settings"
            }
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
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
                    SettingsView(
                        onProfileClick = { selectedTab = DashboardTab.PROFILE },
                        onLogout = onLogout)
                }
                DashboardTab.PROFILE -> {
                    // This is the new Profile View we built
                    ProfileView(
                        state = profileState,
                        onRetry = onLoadProfile
                    )
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

@Composable
fun ProfileInfoRow(label: String, value: String, icon: ImageVector) {
    ListItem(
        headlineContent = { Text(label, style = MaterialTheme.typography.labelMedium, color = Color.Gray) },
        supportingContent = { Text(value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold) },
        leadingContent = { Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp)) }
    )
}

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
            onLogout = {},
            onLoadProfile = {},
            profileState = Resource.Loading()
        )
    }
}