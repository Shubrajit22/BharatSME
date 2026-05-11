package com.example.bharatsme.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.example.bharatsme.ui.auth.components.AuthToggleButton
import com.example.bharatsme.ui.auth.viewmodel.AuthMode
import com.example.bharatsme.ui.auth.viewmodel.AuthViewModel
import com.example.bharatsme.ui.auth.viewmodel.UserType
import com.example.bharatsme.ui.theme.BharatSMETheme
import com.example.bharatsme.util.Resource

@Composable
fun AuthScreen(viewModel: AuthViewModel, onAuthSuccess: () -> Unit) {
    val mode by viewModel.mode
    val state by viewModel.authState
    val selectedUserType by viewModel.selectedUserType
    val context = LocalContext.current

    AuthContent(
        mode = mode,
        state = state,
        selectedUserType = selectedUserType,
        onUserTypeChange = viewModel::setUserType,
        onToggleMode = viewModel::toggleMode,
        onLogin = viewModel::login,
        onRegister = viewModel::register,
        onGoogleLogin = { viewModel.continueWithGoogle(context) },
        onAuthSuccess = onAuthSuccess
    )
}

@Composable
fun AuthContent(
    mode: AuthMode,
    state: Resource<Any>?,
    selectedUserType: UserType,
    onUserTypeChange: (UserType) -> Unit,
    onToggleMode: () -> Unit,
    onLogin: (String, String) -> Unit,
    onRegister: (String, String, String, UserType) -> Unit,
    onGoogleLogin: () -> Unit,
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
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant).padding(4.dp)
        ) {
            AuthToggleButton("Login", mode == AuthMode.LOGIN, Modifier.weight(1f)) {
                if (mode != AuthMode.LOGIN) onToggleMode()
            }
            AuthToggleButton("Register", mode == AuthMode.REGISTER, Modifier.weight(1f)) {
                if (mode != AuthMode.REGISTER) onToggleMode()
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Animated Form Fields
        if (mode == AuthMode.REGISTER) {
            Text("Register as:", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth().height(48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f))
                    .padding(4.dp)
            ) {
                AuthToggleButton(
                    text = "Individual",
                    isSelected = selectedUserType == UserType.INDIVIDUAL,
                    modifier = Modifier.weight(1f)
                ) { onUserTypeChange(UserType.INDIVIDUAL) }

                AuthToggleButton(
                    text = "SME",
                    isSelected = selectedUserType == UserType.SME,
                    modifier = Modifier.weight(1f)
                ) { onUserTypeChange(UserType.SME) }
            }
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it },
                label = { Text(if (selectedUserType == UserType.SME) "Business Name" else "Full Name") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) }
            )
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text(if (mode == AuthMode.LOGIN) "Email or Username" else "Email") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) }
            )

            Spacer(modifier = Modifier.height(12.dp))
        }

        OutlinedTextField(
            value = email, // This serves as the 'identifier' for Login
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
                else onRegister(email, password, fullName, selectedUserType)
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(12.dp),
            enabled = state !is Resource.Loading
        ) {
            if (state is Resource.Loading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Text(if (mode == AuthMode.LOGIN) "Login" else "Sign Up")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            HorizontalDivider(modifier = Modifier.weight(1f))
            Text(" OR ", style = MaterialTheme.typography.labelSmall, color = Color.Gray, modifier = Modifier.padding(horizontal = 8.dp))
            HorizontalDivider(modifier = Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = onGoogleLogin,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Continue with Google")
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



@Preview(showBackground = true, name = "Login Mode")
@Composable
fun AuthScreenLoginPreview() {
    BharatSMETheme {
        AuthContent(
            mode = AuthMode.LOGIN,
            state = null,
            onToggleMode = {},
            onLogin = { _, _ -> },
            onRegister = { _, _, _,_ -> },
            onAuthSuccess = {},
            onGoogleLogin = {},
            onUserTypeChange = {},
            selectedUserType = UserType.INDIVIDUAL
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
            onRegister = { _, _, _,_ -> },
            onAuthSuccess = {},
            onGoogleLogin = {},
            onUserTypeChange = {},
            selectedUserType = UserType.INDIVIDUAL
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
            onRegister = { _, _, _,_ -> },
            onAuthSuccess = {},
            onGoogleLogin = {},
            onUserTypeChange = {},
            selectedUserType = UserType.INDIVIDUAL
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
            onRegister = { _, _, _,_ -> },
            onAuthSuccess = {},
            onGoogleLogin = {},
            onUserTypeChange = {},
            selectedUserType = UserType.INDIVIDUAL
        )
    }
}

