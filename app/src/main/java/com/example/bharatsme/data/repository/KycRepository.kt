package com.example.bharatsme.data.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.bharatsme.data.remote.api.SmeApiService
import com.example.bharatsme.data.remote.dto.BasicDetails
import com.example.bharatsme.data.remote.dto.KycResponse
import com.example.bharatsme.util.NetworkResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class KycRepository(private val api: SmeApiService, private val context: Context) {

    // Helper: Converts a URI into a temporary File object using the class 'context'
    private fun getFileFromUri(uri: Uri, partName: String): File? {
        val contentResolver = context.contentResolver
        val fileName = "${partName}_${System.currentTimeMillis()}.jpg"
        val tempFile = File(context.cacheDir, fileName)

        return try {
            contentResolver.openInputStream(uri)?.use { inputStream ->
                tempFile.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            tempFile
        } catch (e: Exception) {
            Log.e("KYC_REPO", "File conversion failed", e)
            null
        }
    }

    // Helper: Creates a Multipart Part from a URI
    private fun prepareFilePart(partName: String, fileUri: Uri): MultipartBody.Part? {
        val file = getFileFromUri(fileUri, partName) ?: return null
        val mimeType = context.contentResolver.getType(fileUri) ?: "image/jpeg"
        val requestFile = file.asRequestBody(mimeType.toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(partName, file.name, requestFile)
    }

    // Helper: Creates a RequestBody for multipart text fields
    private fun String.toPart(): RequestBody = this.toRequestBody("text/plain".toMediaTypeOrNull())

    // --- 1. Init KYC (Basic Details) ---
    suspend fun initKyc(name: String, email: String): NetworkResponse<String> = withContext(Dispatchers.IO) {
        try {
            val response = api.checkDuplicateAndInit(BasicDetails(name, email))
            if (response.isSuccessful) {
                val body = response.body()
                // FIX: The FastAPI backend returns "applicationId", not "id"
                val receivedId = body?.applicationId ?: ""
                NetworkResponse.Success(receivedId)
            } else {
                NetworkResponse.Error("Backend Error: ${response.code()}")
            }
        } catch (e: Exception) {
            NetworkResponse.Error(e.localizedMessage ?: "Parsing Error")
        }
    }

    // --- 2. PAN Validation ---
    suspend fun validatePan(
        appId: String,
        panNum: String,
        panUri: Uri
    ): NetworkResponse<KycResponse> = withContext(Dispatchers.IO) {
        try {
            val panPart = prepareFilePart("file", panUri)
                ?: return@withContext NetworkResponse.Error("Could not read image file")

            // Backend (FastAPI) expects Form fields for these
            val response = api.validatePan(
                applicationId = appId.toPart(),
                panNumber = panNum.toPart(),
                file = panPart
            )

            if (response.isSuccessful && response.body() != null) {
                NetworkResponse.Success(response.body()!!)
            } else {
                NetworkResponse.Error("Server Error: ${response.code()}")
            }
        } catch (e: Exception) {
            NetworkResponse.Error(e.localizedMessage ?: "Network connection failed")
        }
    }

    // --- 3. Aadhaar Validation ---
    suspend fun validateAadhaar(
        appId: String,
        aadhaarNum: String,
        frontUri: Uri,
        backUri: Uri
    ): NetworkResponse<Unit> = withContext(Dispatchers.IO) {
        try {
            val frontPart = prepareFilePart("frontImage", frontUri)!!
            val backPart = prepareFilePart("backImage", backUri)!!

            val response = api.validateAadhaar(
                applicationId = appId.toPart(),
                aadhaarNumber = aadhaarNum.toPart(),
                frontImage = frontPart,
                backImage = backPart
            )

            if (response.isSuccessful) NetworkResponse.Success(Unit)
            else NetworkResponse.Error("Aadhaar validation failed")
        } catch (e: Exception) {
            NetworkResponse.Error(e.message ?: "Network error during Aadhaar upload")
        }
    }

    // --- 4. Photo & Signature Upload ---
    suspend fun uploadBiometrics(
        appId: String,
        photoUri: Uri,
        signUri: Uri
    ): NetworkResponse<Unit> = withContext(Dispatchers.IO) {
        try {
            val photoPart = prepareFilePart("photo", photoUri)!!
            val signPart = prepareFilePart("signature", signUri)!!

            val response = api.uploadPhotoSignature(
                applicationId = appId.toPart(),
                photo = photoPart,
                signature = signPart
            )

            if (response.isSuccessful) NetworkResponse.Success(Unit)
            else NetworkResponse.Error("Biometrics upload failed")
        } catch (e: Exception) {
            NetworkResponse.Error(e.message ?: "Network error during Biometrics upload")
        }
    }
}