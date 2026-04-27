package com.example.bharatsme.ui.auth

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bharatsme.data.remote.dto.LoginRequest
import com.example.bharatsme.data.remote.dto.RegisterRequest
import com.example.bharatsme.data.repository.AuthRepository
import com.example.bharatsme.util.Resource
import kotlinx.coroutines.launch

enum class AuthMode { LOGIN, REGISTER }

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _authState = mutableStateOf<Resource<Any>?>(null)
    val authState: State<Resource<Any>?> = _authState

    private val _mode = mutableStateOf(AuthMode.LOGIN)
    val mode: State<AuthMode> = _mode

    fun toggleMode() {
        _mode.value = if (_mode.value == AuthMode.LOGIN) AuthMode.REGISTER else AuthMode.LOGIN
        _authState.value = null // Clear errors when switching
    }

    fun login(identifier: String, pass: String) {
        viewModelScope.launch {
            _authState.value = Resource.Loading()
            _authState.value = repository.login(LoginRequest(identifier, pass))
        }
    }

    fun register(email: String, pass: String, name: String) {
        viewModelScope.launch {
            _authState.value = Resource.Loading()
            _authState.value = repository.register(RegisterRequest(email, pass, name))
        }
    }
}