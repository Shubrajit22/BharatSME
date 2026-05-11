package com.example.bharatsme.ui.dashboard

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.bharatsme.data.remote.dto.LoanResponse
import com.example.bharatsme.data.remote.dto.UserProfile
import com.example.bharatsme.ui.dashboard.components.DashboardGridView
import com.example.bharatsme.ui.dashboard.components.ProfileView
import com.example.bharatsme.ui.dashboard.components.SettingsView
import com.example.bharatsme.ui.dashboard.viewmodel.DashboardViewModel
import com.example.bharatsme.ui.theme.BharatSMETheme
import com.example.bharatsme.util.Resource

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
@OptIn(ExperimentalMaterial3Api::class)
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
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = when(selectedTab) {
                                DashboardTab.HOME -> "Hello, $userName"
                                DashboardTab.PROFILE -> "My Profile"
                                DashboardTab.SETTINGS -> "Settings"
                            },
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        if (selectedTab == DashboardTab.HOME) {
                            Text(
                                "Welcome to BharatSME Portal",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                        }
                    }
                },
                actions = {
                    // Profile Icon shortcut in the top right
                    if (selectedTab != DashboardTab.PROFILE) {
                        IconButton(onClick = { selectedTab = DashboardTab.PROFILE }) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Go to Profile",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
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