package com.example.bharatsme.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.example.bharatsme.ui.theme.BharatSMETheme
import com.example.bharatsme.util.Resource

@Composable
fun AuthScreen(viewModel: AuthViewModel, onAuthSuccess: () -> Unit) {
    val mode by viewModel.mode
    val state by viewModel.authState

    AuthContent(
        mode = mode,
        state = state,
        onToggleMode = viewModel::toggleMode,
        onLogin = viewModel::login,
        onRegister = viewModel::register,
        onAuthSuccess = onAuthSuccess
    )
}

@Composable
fun AuthContent(
    mode: AuthMode,
    state: Resource<Any>?,
    onToggleMode: () -> Unit,
    onLogin: (String, String) -> Unit,
    onRegister: (String, String, String) -> Unit,
    onAuthSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Form States
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }

    // Handle Success Navigation
    LaunchedEffect(state) {
        if (state is Resource.Success) {
            onAuthSuccess()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = if (mode == AuthMode.LOGIN) "Welcome Back" else "Create Account",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = "BharatSME Loan Portal",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Toggle Switch
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(4.dp)
        ) {
            AuthToggleButton(
                text = "Login",
                isSelected = mode == AuthMode.LOGIN,
                modifier = Modifier.weight(1f),
                onClick = { if (mode != AuthMode.LOGIN) onToggleMode() }
            )
            AuthToggleButton(
                text = "Register",
                isSelected = mode == AuthMode.REGISTER,
                modifier = Modifier.weight(1f),
                onClick = { if (mode != AuthMode.REGISTER) onToggleMode() }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Animated Form Fields
        if (mode == AuthMode.REGISTER) {
            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it },
                label = { Text("Full Name") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text(if (mode == AuthMode.LOGIN) "Email or Username" else "Email") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) }
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Action Button
        Button(
            onClick = {
                if (mode == AuthMode.LOGIN) onLogin(email, password)
                else onRegister(email, password, fullName)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            enabled = state !is Resource.Loading
        ) {
            if (state is Resource.Loading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Text(if (mode == AuthMode.LOGIN) "Login" else "Sign Up")
            }
        }

        // Error Message
        if (state is Resource.Error) {
            Text(
                text = state.message ?: "Error",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 16.dp),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun AuthToggleButton(text: String, isSelected: Boolean, modifier: Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Preview(showBackground = true, name = "Login Mode")
@Composable
fun AuthScreenLoginPreview() {
    BharatSMETheme {
        AuthContent(
            mode = AuthMode.LOGIN,
            state = null,
            onToggleMode = {},
            onLogin = { _, _ -> },
            onRegister = { _, _, _ -> },
            onAuthSuccess = {}
        )
    }
}

@Preview(showBackground = true, name = "Register Mode")
@Composable
fun AuthScreenRegisterPreview() {
    BharatSMETheme {
        AuthContent(
            mode = AuthMode.REGISTER,
            state = null,
            onToggleMode = {},
            onLogin = { _, _ -> },
            onRegister = { _, _, _ -> },
            onAuthSuccess = {}
        )
    }
}

@Preview(showBackground = true, name = "Loading State")
@Composable
fun AuthScreenLoadingPreview() {
    BharatSMETheme {
        AuthContent(
            mode = AuthMode.LOGIN,
            state = Resource.Loading(),
            onToggleMode = {},
            onLogin = { _, _ -> },
            onRegister = { _, _, _ -> },
            onAuthSuccess = {}
        )
    }
}

@Preview(showBackground = true, name = "Error State")
@Composable
fun AuthScreenErrorPreview() {
    BharatSMETheme {
        AuthContent(
            mode = AuthMode.LOGIN,
            state = Resource.Error("Invalid credentials"),
            onToggleMode = {},
            onLogin = { _, _ -> },
            onRegister = { _, _, _ -> },
            onAuthSuccess = {}
        )
    }
}

