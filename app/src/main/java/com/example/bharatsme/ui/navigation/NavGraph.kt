package com.example.bharatsme.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.bharatsme.data.repository.AuthRepository
import com.example.bharatsme.data.repository.KycRepository
import com.example.bharatsme.data.repository.LoanRepository
import com.example.bharatsme.ui.auth.AuthScreen
import com.example.bharatsme.ui.auth.AuthViewModel
import com.example.bharatsme.ui.dashboard.DashboardScreen
import com.example.bharatsme.ui.dashboard.DashboardViewModel
import com.example.bharatsme.ui.kyc.KycScreen
import com.example.bharatsme.ui.kyc.KycViewModel


@Composable
fun NavGraph(
    authRepository: AuthRepository,
    loanRepository: LoanRepository,
    kycRepository: KycRepository,
    userName: String // Pass this after successful login
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Auth
    ) {
        // --- Authentication Screen ---
        composable<Screen.Auth> {
            val viewModel: AuthViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return AuthViewModel(authRepository) as T
                    }
                }
            )

            AuthScreen(
                viewModel = viewModel,
                onAuthSuccess = {
                    navController.navigate(Screen.Dashboard) {
                        // Pop Auth screen so user can't go back to login
                        popUpTo(Screen.Auth) { inclusive = true }
                    }
                }
            )
        }

        // --- Dashboard Screen ---
        composable<Screen.Dashboard> {
            val viewModel: DashboardViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return DashboardViewModel(loanRepository) as T
                    }
                }
            )

            DashboardScreen(
                userName = userName,
                viewModel = viewModel,
                onNavigateToKyc = { navController.navigate(Screen.Kyc) },
                onNavigateToNewLoan = { navController.navigate(Screen.NewLoan) }
            )
        }

        // --- KYC Screen ---
        composable<Screen.Kyc> {
            val viewModel: KycViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return KycViewModel(kycRepository) as T
                    }
                }
            )

            KycScreen(
                viewModel = viewModel,
                onComplete = {
                    // Navigate back to Dashboard and clear the KYC screen from stack
                    navController.navigate(Screen.Dashboard) {
                        popUpTo(Screen.Kyc) { inclusive = true }
                    }
                }
            )
        }

        // --- New Loan Screen (Placeholder) ---
        composable<Screen.NewLoan> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Loan Application coming soon...")
            }
        }
    }
}