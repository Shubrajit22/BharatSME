package com.example.bharatsme.ui.kyc.viewmodel

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.bharatsme.data.remote.dto.KycResponse
import com.example.bharatsme.data.repository.KycRepository

import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

sealed interface VerificationUiState {
    /** Initial state before any action is taken */
    object Idle : VerificationUiState

    /** State while the image is being processed by the AI engine */
    object Loading : VerificationUiState

    /**
     * Success state containing the verified data.
     * @param data The parsed response from the FastAPI server
     */
    data class Success(val data: KycResponse) : VerificationUiState

    /**
     * Error state for network failures or validation rejections.
     * @param message A user-friendly error message
     */
    data class Error(val message: String) : VerificationUiState
}
class VerificationViewModel(private val repository: KycRepository) : ViewModel() {

    var uiState by mutableStateOf<VerificationUiState>(VerificationUiState.Idle)
        private set

}



fun Context.getFileFromUri(uri: Uri): File {
    val tempFile = File(cacheDir, "temp_kyc_image.jpg")
    tempFile.createNewFile()

    val inputStream: InputStream? = contentResolver.openInputStream(uri)
    val outputStream = FileOutputStream(tempFile)

    inputStream?.use { input ->
        outputStream.use { output ->
            input.copyTo(output)
        }
    }
    return tempFile
}