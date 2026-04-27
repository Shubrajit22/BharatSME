package com.example.bharatsme.ui.loans

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.bharatsme.ui.components.SmeDropdown
import com.example.bharatsme.util.Resource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewLoanScreen(viewModel: LoanViewModel, onBack: () -> Unit, onAuthSuccess: () -> Unit) {
    val state by viewModel.loanState

    // Form States
    var applicantName by remember { mutableStateOf("") }
    var businessType by remember { mutableStateOf("Retail") }
    var turnoverBand by remember { mutableStateOf("0-10L") }
    var amount by remember { mutableStateOf("") }
    var years by remember { mutableStateOf("") }

    val businessTypes = listOf("Retail", "Manufacturing", "Services", "Wholesale")
    val turnoverBands = listOf("0-10L", "10-50L", "50L-1Cr", "1Cr+")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("New Loan Application") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = applicantName,
                onValueChange = { applicantName = it },
                label = { Text("Applicant/Business Name") },
                modifier = Modifier.fillMaxWidth()
            )

            // Business Type Dropdown
            SmeDropdown(label = "Business Type", options = businessTypes, selected = businessType) {
                businessType = it
            }

            // Turnover Band Dropdown
            SmeDropdown(label = "Annual Turnover", options = turnoverBands, selected = turnoverBand) {
                turnoverBand = it
            }

            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Requested Amount (₹)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            OutlinedTextField(
                value = years,
                onValueChange = { years = it },
                label = { Text("Years in Business") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    viewModel.applyForLoan(applicantName, businessType, turnoverBand, amount, years.toIntOrNull() ?: 0)
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = state !is Resource.Loading && amount.isNotEmpty()
            ) {
                if (state is Resource.Loading) CircularProgressIndicator(color = Color.White)
                else Text("Submit Application")
            }

            if (state is Resource.Success) {
                Text("Application Submitted! ID: ${state?.data?.id}", color = Color(0xFF2E7D32))
            }
        }
    }
}