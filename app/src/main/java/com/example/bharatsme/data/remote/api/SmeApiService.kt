package com.example.bharatsme.data.remote.api

import com.example.bharatsme.data.remote.dto.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface SmeApiService {

    // --- Authentication ---
    @POST("api/v1/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<UserResponse>

    @POST("api/v1/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<TokenResponse>

    // --- KYC Onboarding ---
    @POST("api/v1/kyc/basic-details")
    suspend fun checkDuplicateAndInit(@Body request: BasicDetails): Response<Unit>

    @Multipart
    @POST("api/v1/kyc/validate-pan")
    suspend fun validatePan(
        @Part("applicationId") applicationId: RequestBody,
        @Part("panNumber") panNumber: RequestBody,
        @Part file: MultipartBody.Part
    ): Response<Unit>

    @Multipart
    @POST("api/v1/kyc/validate-aadhaar")
    suspend fun validateAadhaar(
        @Part("applicationId") applicationId: RequestBody,
        @Part("aadhaarNumber") aadhaarNumber: RequestBody,
        @Part frontImage: MultipartBody.Part,
        @Part backImage: MultipartBody.Part
    ): Response<Unit>

    @Multipart
    @POST("api/v1/kyc/upload-photo-signature")
    suspend fun uploadPhotoSignature(
        @Part("applicationId") applicationId: RequestBody,
        @Part photo: MultipartBody.Part,
        @Part signature: MultipartBody.Part
    ): Response<Unit>

    @POST("api/v1/kyc/submit")
    suspend fun submitKyc(@Body request: SubmitKyc): Response<Unit>

    // --- Loans Management ---
    @GET("api/v1/loans/")
    suspend fun listApps(): Response<List<LoanResponse>>

    @POST("api/v1/loans/")
    suspend fun createApp(@Body request: LoanCreate): Response<LoanResponse>

    @POST("api/v1/loans/{id}/evaluate")
    suspend fun runPrescreen(@Path("id") id: String): Response<Unit>

    @POST("api/v1/loans/apply")
    suspend fun applyForLoan(@Body request: LoanCreate): Response<Unit>
}