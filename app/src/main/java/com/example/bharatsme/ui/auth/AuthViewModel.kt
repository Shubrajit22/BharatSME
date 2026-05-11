package com.example.bharatsme.ui.auth

import android.content.Context
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bharatsme.data.remote.dto.LoginRequest
import com.example.bharatsme.data.remote.dto.RegisterRequest
import com.example.bharatsme.data.repository.AuthRepository
import com.example.bharatsme.util.Resource
import kotlinx.coroutines.launch
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.example.bharatsme.data.remote.dto.GoogleLoginRequest
import com.example.bharatsme.util.Constants
import com.google.android.libraries.identity.googleid.GetGoogleIdOption

enum class AuthMode { LOGIN, REGISTER }
enum class UserType { INDIVIDUAL, SME }

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _authState = mutableStateOf<Resource<Any>?>(null)
    val authState: State<Resource<Any>?> = _authState

    private val _mode = mutableStateOf(AuthMode.LOGIN)
    val mode: State<AuthMode> = _mode

    private val _selectedUserType = mutableStateOf(UserType.INDIVIDUAL)
    val selectedUserType: State<UserType> = _selectedUserType

    fun toggleMode() {
        _mode.value = if (_mode.value == AuthMode.LOGIN) AuthMode.REGISTER else AuthMode.LOGIN
        _authState.value = null // Clear errors when switching
    }

    fun setUserType(type: UserType) {
        _selectedUserType.value = type
    }

    fun login(identifier: String, pass: String) {
        viewModelScope.launch {
            _authState.value = Resource.Loading()
            _authState.value = repository.login(LoginRequest(identifier, pass))
        }
    }

    fun register(email: String, pass: String, name: String, userType: UserType) {
        viewModelScope.launch {
            _authState.value = Resource.Loading()
            // Ensure your RegisterRequest DTO on the Android side matches the backend 'userType' field
            _authState.value = repository.register(
                RegisterRequest(email, pass, name, userType.name)
            )
        }
    }
    fun continueWithGoogle() {
        // Logic for Google Sign-In Credential Manager goes here for the demo
        viewModelScope.launch {
            _authState.value = Resource.Loading()
            // Placeholder for Google Auth repository call
        }
    }

    fun continueWithGoogle(context: Context) {
        viewModelScope.launch {
            _authState.value = Resource.Loading()

            val credentialManager = CredentialManager.create(context)

            // 1. Configure Google ID Token Request
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(Constants.GOOGLE_CLIENT_ID) // Must match your .env GOOGLE_CLIENT_ID
                .setAutoSelectEnabled(true)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            try {
                // 2. Execute the Request
                val result = credentialManager.getCredential(context, request)
                val googleIdToken = result.credential.data.getString("com.google.android.libraries.identity.googleid.BUNDLE_KEY_ID_TOKEN")

                if (googleIdToken != null) {
                    // 3. Send to Backend with the selected UserType
                    val authResult = repository.googleLogin(
                        GoogleLoginRequest(
                            idToken = googleIdToken,
                            userType = _selectedUserType.value.name
                        )
                    )
                    _authState.value = authResult
                }
            } catch (e: Exception) {
                Log.e("GOOGLE_AUTH", "Detailed Failure: ${e.message}", e)
                _authState.value = Resource.Error("Google Sign-In Failed: ${e.localizedMessage}")
            }
        }
    }
}