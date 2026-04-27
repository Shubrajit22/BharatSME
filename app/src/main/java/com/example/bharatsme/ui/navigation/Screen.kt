package com.example.bharatsme.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class Screen {
    @Serializable data object Auth : Screen()
    @Serializable data object Dashboard : Screen()
    @Serializable data object Kyc : Screen()
    @Serializable data object NewLoan : Screen()
}