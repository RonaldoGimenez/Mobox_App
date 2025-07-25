package com.example.mobox_app.ui.screens.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.mobox_app.data.AppRepository
import com.example.mobox_app.MoboxApp // AJUSTE: NUEVO - Importar MoboxApp
import kotlinx.coroutines.launch

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val emailError: String? = null,
    val passwordError: String? = null,
    val isLoading: Boolean = false,
    val loginError: String? = null,
    val isLoginEnabled: Boolean = false
)

class LoginViewModel(
    private val repository: AppRepository,
    private val application: MoboxApp // AJUSTE: NUEVO - Añadir MoboxApp como dependencia
) : ViewModel() {

    var uiState by mutableStateOf(LoginUiState())
        private set

    fun onEmailChange(email: String) {
        uiState = uiState.copy(
            email = email,
            emailError = null,
            loginError = null
        )
        updateLoginEnabled()
    }

    fun onPasswordChange(password: String) {
        uiState = uiState.copy(
            password = password,
            passwordError = null,
            loginError = null
        )
        updateLoginEnabled()
    }

    private fun updateLoginEnabled() {
        uiState = uiState.copy(
            isLoginEnabled = uiState.email.isNotBlank() &&
                    uiState.password.isNotBlank()
        )
    }

    fun onLoginClick(onLoginSuccess: () -> Unit) {
        if (!validateInput()) return

        viewModelScope.launch {
            try {
                uiState = uiState.copy(isLoading = true, loginError = null)

                val user = repository.getUserByEmail(uiState.email.trim())

                if (user != null && user.passwordHash == uiState.password) {
                    println("Login exitoso: ${user.name}")
                    application.currentUser = user // AJUSTE: NUEVO - ASIGNAR EL USUARIO LOGUEADO
                    uiState = uiState.copy(isLoading = false)
                    onLoginSuccess()
                } else {
                    uiState = uiState.copy(
                        isLoading = false,
                        loginError = "Email o contraseña incorrectos"
                    )
                }
            } catch (e: Exception) {
                uiState = uiState.copy(
                    isLoading = false,
                    loginError = "Error de conexión. Intenta nuevamente."
                )
                println("Error en login: ${e.message}")
            }
        }
    }

    private fun validateInput(): Boolean {
        val emailValidation = validateEmail(uiState.email)
        val passwordValidation = validatePassword(uiState.password)

        uiState = uiState.copy(
            emailError = emailValidation,
            passwordError = passwordValidation
        )

        return emailValidation == null && passwordValidation == null
    }

    private fun validateEmail(email: String): String? {
        return when {
            email.isBlank() -> "El email es requerido"
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Email inválido"
            else -> null
        }
    }

    private fun validatePassword(password: String): String? {
        return when {
            password.isBlank() -> "La contraseña es requerida"
            password.length < 6 -> "La contraseña debe tener al menos 6 caracteres"
            else -> null
        }
    }

    fun clearErrors() {
        uiState = uiState.copy(
            emailError = null,
            passwordError = null,
            loginError = null
        )
    }
}


class LoginViewModelFactory(
    private val repository: AppRepository,
    private val application: MoboxApp
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(repository, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}