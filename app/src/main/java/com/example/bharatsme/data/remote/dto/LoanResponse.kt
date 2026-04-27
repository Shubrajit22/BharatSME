package com.example.bharatsme.data.remote.dto

data class LoanResponse(
    val id: String,
    val applicantName: String,
    val businessType: String,
    val turnoverBand: String,
    val requestedLoanAmount: String,
    val yearsInBusiness: Int,
    val eligibilityStatus: String,
    val preScreenResult: String,
    val lockedByStaff: Boolean,
    val createdAt: String,
    val updatedAt: String
)
