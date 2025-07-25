package com.example.mobox_app.ui.screens.register

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mobox_app.MoboxApp
import com.example.mobox_app.ui.components.MoboxTextField
import com.example.mobox_app.ui.theme.InputBackground

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onLoginClick: () -> Unit,
    onRegisterSuccess: () -> Unit
) {
    val application = LocalContext.current.applicationContext as MoboxApp
    val viewModel: RegisterViewModel = viewModel(
        factory = RegisterViewModelFactory(repository = application.repository)
    )

    val uiState = viewModel.uiState

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()), // Permite el scroll si el contenido es largo
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // Título
            Text(
                text = "MOBOX",
                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Crear Cuenta",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "Regístrate para comenzar",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Campo de Nombre
            MoboxTextField(
                value = uiState.name,
                onValueChange = viewModel::onNameChange,
                labelText = "Nombre",
                modifier = Modifier.fillMaxWidth(),
                isError = uiState.nameError != null
            )
            if (uiState.nameError != null) {
                ErrorText(uiState.nameError)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Campo de Apellido
            MoboxTextField(
                value = uiState.lastName,
                onValueChange = viewModel::onLastNameChange,
                labelText = "Apellido",
                modifier = Modifier.fillMaxWidth(),
                isError = uiState.lastNameError != null
            )
            if (uiState.lastNameError != null) {
                ErrorText(uiState.lastNameError)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Campo de Email
            MoboxTextField(
                value = uiState.email,
                onValueChange = viewModel::onEmailChange,
                labelText = "Correo",
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                isError = uiState.emailError != null
            )
            if (uiState.emailError != null) {
                ErrorText(uiState.emailError)
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
            if (uiState.passwordError != null) {
                ErrorText(uiState.passwordError)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Campo de Confirmar Contraseña
            MoboxPasswordField(
                value = uiState.confirmPassword,
                onValueChange = viewModel::onConfirmPasswordChange,
                labelText = "Confirmar contraseña",
                modifier = Modifier.fillMaxWidth(),
                isError = uiState.confirmPasswordError != null
            )
            if (uiState.confirmPasswordError != null) {
                ErrorText(uiState.confirmPasswordError)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Error general de registro
            if (uiState.registerError != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = uiState.registerError,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Información sobre requisitos de contraseña
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "La contraseña debe contener:",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    PasswordRequirement("Al menos 6 caracteres", uiState.password.length >= 6)
                    PasswordRequirement("Al menos una mayúscula", uiState.password.matches(Regex(".*[A-Z].*")))
                    PasswordRequirement("Al menos una minúscula", uiState.password.matches(Regex(".*[a-z].*")))
                    PasswordRequirement("Al menos un número", uiState.password.matches(Regex(".*[0-9].*")))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Botón de Registro
            Button(
                onClick = { viewModel.onRegisterClick(onRegisterSuccess) },
                modifier = Modifier.fillMaxWidth(),
                enabled = uiState.isRegisterEnabled && !uiState.isLoading
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(if (uiState.isLoading) "Registrando..." else "Registrar")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Enlace a login
            TextButton(onClick = onLoginClick) {
                Text("¿Ya tienes cuenta? Iniciar Sesión")
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun ErrorText(error: String) {
    Text(
        text = error,
        color = MaterialTheme.colorScheme.error,
        style = MaterialTheme.typography.labelSmall,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, top = 4.dp)
    )
}

@Composable
private fun PasswordRequirement(
    requirement: String,
    isMet: Boolean
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = if (isMet) "✓" else "•",
            color = if (isMet) Color(0xFF4CAF50) else MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.labelSmall
        )
        Text(
            text = requirement,
            style = MaterialTheme.typography.labelSmall,
            color = if (isMet) Color(0xFF4CAF50) else MaterialTheme.colorScheme.onSurfaceVariant
        )
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
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = InputBackground,
            unfocusedContainerColor = InputBackground,
            focusedBorderColor = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = if (isError) MaterialTheme.colorScheme.error else Color.Transparent,
            focusedLabelColor = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
            unfocusedLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        ),
        isError = isError,
        singleLine = true
    )
}