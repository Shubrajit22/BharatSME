package com.example.bharatsme

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.example.bharatsme.data.local.TokenManager
import com.example.bharatsme.data.remote.RetrofitClient
import com.example.bharatsme.data.repository.AuthRepository
import com.example.bharatsme.data.repository.KycRepository
import com.example.bharatsme.data.repository.LoanRepository
import com.example.bharatsme.ui.navigation.NavGraph
import com.example.bharatsme.ui.theme.BharatSMETheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Manual DI Setup
        val tokenManager = TokenManager(applicationContext)
        val apiService = RetrofitClient.getApiService(tokenManager)
        val authRepo = AuthRepository(apiService, tokenManager)
        val loanRepo = LoanRepository(apiService)
        val kycRepo = KycRepository(apiService, applicationContext)

        setContent {
            BharatSMETheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    // 2. Launch the NavGraph
                    NavGraph(
                        authRepository = authRepo,
                        loanRepository = loanRepo,
                        kycRepository = kycRepo,
                        userName = "Applicant" // You can fetch this from DataStore later
                    )
                }
            }
        }
    }
}