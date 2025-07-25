package com.example.mobox_app.ui.screens.login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mobox_app.MoboxApp
import com.example.mobox_app.ui.components.MoboxTextField

@Composable
fun LoginScreen(
    onRegisterClick: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    val application = LocalContext.current.applicationContext as MoboxApp
    val viewModel: LoginViewModel = viewModel(
        factory = LoginViewModelFactory(
            repository = application.repository,
            application = application // AJUSTE: NUEVO - PASAR LA INSTANCIA DE APPLICATION
        )
    )
    val uiState = viewModel.uiState
    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Título
            Text(
                text = "MOBOX",
                style = MaterialTheme.typography.headlineLarge.copy(fontWeight =
                    FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Bienvenido",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight =
                    FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Te estábamos esperando",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.height(32.dp))
            // Campo de Email
            MoboxTextField(
                value = uiState.email,
                onValueChange = viewModel::onEmailChange,
                labelText = "Correo",
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType =
                    KeyboardType.Email),
                isError = uiState.emailError != null
            )
            // Error de email
            if (uiState.emailError != null) {
                Text(
                    text = uiState.emailError,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, top = 4.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            // Campo de Contraseña
            MoboxPasswordField(
                value = uiState.password,
                onValueChange = viewModel::onPasswordChange,
                labelText = "Contraseña",
                modifier = Modifier.fillMaxWidth(),
                isError = uiState.passwordError != null
            )
            // Error de contraseña
            if (uiState.passwordError != null) {
                Text(
                    text = uiState.passwordError,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, top = 4.dp)
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            // Error general de login
            if (uiState.loginError != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = uiState.loginError,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            // Botón de Login
            Button(
                onClick = { viewModel.onLoginClick(onLoginSuccess) },
                modifier = Modifier.fillMaxWidth(),
                enabled = uiState.isLoginEnabled && !uiState.isLoading
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (uiState.isLoading) "Ingresando..." else "Iniciar Sesión")
            }
            Spacer(modifier = Modifier.height(16.dp))
            // Enlace a registro
            TextButton(onClick = onRegisterClick) {
                Text("¿No tienes cuenta? Crear una")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MoboxPasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    labelText: String,
    modifier: Modifier = Modifier,
    isError: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        label = { Text(labelText) },
        visualTransformation = PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType =
            KeyboardType.Password),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor =
                com.example.mobox_app.ui.theme.InputBackground,
            unfocusedContainerColor =
                com.example.mobox_app.ui.theme.InputBackground,
            focusedBorderColor = if (isError) MaterialTheme.colorScheme.error else
                MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = if (isError) MaterialTheme.colorScheme.error else
                androidx.compose.ui.graphics.Color.Transparent,
            focusedLabelColor = if (isError) MaterialTheme.colorScheme.error else
                MaterialTheme.colorScheme.primary,
            unfocusedLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha =
                0.6f)
        ),
        isError = isError,
        singleLine = true
    )
}