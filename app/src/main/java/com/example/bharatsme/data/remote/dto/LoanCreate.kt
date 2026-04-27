package com.example.bharatsme.data.remote.dto

// --- Loan DTOs ---
data class LoanCreate(
    val applicantName: String,
    val businessType: String,
    val turnoverBand: String,
    val requestedLoanAmount: String, // FastAPI schema says anyOf [number, string]
    val yearsInBusiness: Int
)