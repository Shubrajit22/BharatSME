package com.example.bharatsme.ui.dashboard.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FactCheck
import androidx.compose.material.icons.filled.AddBusiness
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.bharatsme.data.remote.dto.LoanResponse
import com.example.bharatsme.util.Resource

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