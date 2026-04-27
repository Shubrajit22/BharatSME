package com.example.bharatsme.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
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
import com.example.bharatsme.ui.loans.LoanViewModel
import com.example.bharatsme.ui.loans.NewLoanScreen
import kotlinx.coroutines.launch


@Composable
fun NavGraph(
    authRepository: AuthRepository,
    loanRepository: LoanRepository,
    kycRepository: KycRepository,
    userName: String,
    startDestination: Any
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = startDestination
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

                        popUpTo(Screen.Auth) { inclusive = true }
                    }
                }
            )
        }

        // --- Dashboard Screen ---
        composable<Screen.Dashboard> {
            val scope = rememberCoroutineScope() // Needed to call suspend logout()

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
                onNavigateToNewLoan = { navController.navigate(Screen.NewLoan) },
                onLogout = {
                    scope.launch {
                        authRepository.logout() // Clears TokenManager
                        navController.navigate(Screen.Auth) {
                            // Clear the entire backstack so they can't "back" into the dashboard
                            popUpTo(0) { inclusive = true }
                        }
                    }
                }
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
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // --- New Loan Screen (Placeholder) ---
        composable<Screen.NewLoan> {
            val viewModel: LoanViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return LoanViewModel(loanRepository) as T
                    }
                }
            )

            NewLoanScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onAuthSuccess = {
                    navController.navigate(Screen.Dashboard) {
                        popUpTo(Screen.Auth) { inclusive = true }
                    }
                }
            )
        }
    }
}