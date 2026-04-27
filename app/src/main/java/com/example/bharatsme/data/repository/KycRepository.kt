package com.example.bharatsme.data.repository

import android.content.Context
import android.net.Uri
import com.example.bharatsme.data.remote.api.SmeApiService
import com.example.bharatsme.data.remote.dto.BasicDetails
import com.example.bharatsme.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream


class KycRepository(private val api: SmeApiService, private val context: Context) {

    // Helper: Converts a URI into a temporary File object
    private fun uriToFile(uri: Uri, fileName: String): File? {
        return try {
            val contentResolver = context.contentResolver
            val inputStream = contentResolver.openInputStream(uri) ?: return null
            val file = File(context.cacheDir, fileName)
            val outputStream = FileOutputStream(file)
            inputStream.copyTo(outputStream)
            inputStream.close()
            outputStream.close()
            file
        } catch (e: Exception) {
            null
        }
    }

    // Helper: Creates a Multipart Part from a URI
    private fun prepareFilePart(partName: String, fileUri: Uri): MultipartBody.Part? {
        val file = uriToFile(fileUri, "$partName.jpg") ?: return null
        val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(partName, file.name, requestFile)
    }

    // Helper: Creates a RequestBody for a simple string (required for multipart fields)
    private fun String.toPart(): RequestBody = this.toRequestBody("text/plain".toMediaTypeOrNull())

    // --- Aadhaar Validation ---
    suspend fun validateAadhaar(
        appId: String,
        aadhaarNum: String,
        frontUri: Uri,
        backUri: Uri
    ): Resource<Unit> {
        return try {
            val frontPart = prepareFilePart("frontImage", frontUri)!!
            val backPart = prepareFilePart("backImage", backUri)!!

            val response = api.validateAadhaar(
                applicationId = appId.toPart(),
                aadhaarNumber = aadhaarNum.toPart(),
                frontImage = frontPart,
                backImage = backPart
            )

            if (response.isSuccessful) Resource.Success(Unit)
            else Resource.Error("Aadhaar validation failed")
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error during Aadhaar upload")
        }
    }

    // --- Photo & Signature Upload ---
    suspend fun uploadBiometrics(
        appId: String,
        photoUri: Uri,
        signUri: Uri
    ): Resource<Unit> {
        return try {
            val photoPart = prepareFilePart("photo", photoUri)!!
            val signPart = prepareFilePart("signature", signUri)!!

            val response = api.uploadPhotoSignature(
                applicationId = appId.toPart(),
                photo = photoPart,
                signature = signPart
            )

            if (response.isSuccessful) Resource.Success(Unit)
            else Resource.Error("Biometrics upload failed")
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error during Biometrics upload")
        }
    }

    // Inside KycRepository.kt
    suspend fun initKyc(name: String, email: String): Resource<Unit> {
        return try {
            val response = api.checkDuplicateAndInit(BasicDetails(name, email))
            if (response.isSuccessful) {
                Resource.Success(Unit)
            } else {
                Resource.Error("User already exists or initialization failed")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Connection error")
        }
    }

    suspend fun validatePan(
        appId: String,
        panNum: String,
        panUri: Uri
    ): Resource<Unit> = withContext(Dispatchers.IO) {
        try {
            // 1. Convert the URI to a Multipart Part
            val panPart = prepareFilePart("file", panUri) ?: return@withContext Resource.Error("Could not process image")

            // 2. Execute the API call
            val response = api.validatePan(
                applicationId = appId.toPart(),
                panNumber = panNum.toPart(),
                file = panPart
            )

            if (response.isSuccessful) {
                Resource.Success(Unit)
            } else {
                Resource.Error("PAN validation failed: ${response.code()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Connection error to server")
        }
    }
}