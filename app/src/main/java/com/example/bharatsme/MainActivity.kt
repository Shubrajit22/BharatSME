package com.example.bharatsme

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.bharatsme.data.local.TokenManager
import com.example.bharatsme.data.remote.RetrofitClient
import com.example.bharatsme.data.repository.AuthRepository
import com.example.bharatsme.data.repository.KycRepository
import com.example.bharatsme.data.repository.LoanRepository
import com.example.bharatsme.ui.navigation.NavGraph
import com.example.bharatsme.ui.navigation.Screen
import com.example.bharatsme.ui.theme.BharatSMETheme
import kotlinx.coroutines.flow.firstOrNull

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
                // Track the start destination
                var startDestination by remember { mutableStateOf<Any?>(null) }

                // Check for token on launch
                LaunchedEffect(Unit) {
                    val token = tokenManager.getToken().firstOrNull()
                    startDestination = if (token != null) Screen.Dashboard else Screen.Auth
                }

                Surface(color = MaterialTheme.colorScheme.background) {
                    // Only show NavGraph once we know where to go
                    startDestination?.let { destination ->
                        NavGraph(
                            authRepository = authRepo,
                            loanRepository = loanRepo,
                            kycRepository = kycRepo,
                            userName = "Applicant", // You can fetch real name here too
                            startDestination = destination
                        )
                    } ?: run {
                        // Optional: Show a splash screen/loading while checking token
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                }
            }
        }
    }
}