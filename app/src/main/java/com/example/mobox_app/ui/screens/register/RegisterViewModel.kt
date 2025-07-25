package com.example.mobox_app.ui.screens.register

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.mobox_app.data.AppRepository
import com.example.mobox_app.data.User
import kotlinx.coroutines.launch

data class RegisterUiState(
    val name: String = "",
    val lastName: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    // Campos para errores de validación
    val nameError: String? = null,
    val lastNameError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    // Estados de la UI
    val isLoading: Boolean = false,
    val registerError: String? = null,
    val isRegisterEnabled: Boolean = false
)

class RegisterViewModel(private val repository: AppRepository) : ViewModel() {
    var uiState by mutableStateOf(RegisterUiState())
        private set

    fun onNameChange(name: String) {
        uiState = uiState.copy(
            name = name,
            nameError = null,
            registerError = null
        )
        updateRegisterEnabled()
    }

    fun onLastNameChange(lastName: String) {
        uiState = uiState.copy(
            lastName = lastName,
            lastNameError = null,
            registerError = null
        )
        updateRegisterEnabled()
    }

    fun onEmailChange(email: String) {
        uiState = uiState.copy(
            email = email,
            emailError = null,
            registerError = null
        )
        updateRegisterEnabled()
    }

    fun onPasswordChange(password: String) {
        uiState = uiState.copy(
            password = password,
            passwordError = null,
            confirmPasswordError = if (uiState.confirmPassword.isNotEmpty() && password != uiState.confirmPassword) {
                "Las contraseñas no coinciden"
            } else null,
            registerError = null
        )
        updateRegisterEnabled()
    }

    fun onConfirmPasswordChange(confirmPassword: String) {
        uiState = uiState.copy(
            confirmPassword = confirmPassword,
            confirmPasswordError = null,
            registerError = null
        )
        updateRegisterEnabled()
    }

    private fun updateRegisterEnabled() {
        uiState = uiState.copy(
            isRegisterEnabled = uiState.name.isNotBlank() &&
                    uiState.lastName.isNotBlank() &&
                    uiState.email.isNotBlank() &&
                    uiState.password.isNotBlank() &&
                    uiState.confirmPassword.isNotBlank()
        )
    }

    fun onRegisterClick(onRegisterSuccess: () -> Unit) {
        if (!validateAllFields()) return

        viewModelScope.launch {
            try {
                uiState = uiState.copy(isLoading = true, registerError = null)

                // Verificar si el email ya existe
                val existingUser = repository.getUserByEmail(uiState.email.trim())
                if (existingUser != null) {
                    uiState = uiState.copy(
                        isLoading = false,
                        emailError = "Este correo ya está registrado"
                    )
                    return@launch
                }

                // Crear nuevo usuario
                val newUser = User(
                    name = uiState.name.trim(),
                    lastName = uiState.lastName.trim(),
                    email = uiState.email.trim(),
                    passwordHash = uiState.password
                )

                repository.insertUser(newUser)
                println("Usuario ${newUser.name} ${newUser.lastName} registrado con éxito!")

                uiState = uiState.copy(isLoading = false)
                onRegisterSuccess()

            } catch (e: Exception) {
                uiState = uiState.copy(
                    isLoading = false,
                    registerError = "Error al registrar. Intenta nuevamente."
                )
                println("Error en registro: ${e.message}")
            }
        }
    }

    private fun validateAllFields(): Boolean {
        val nameValidation = validateName(uiState.name)
        val lastNameValidation = validateLastName(uiState.lastName)
        val emailValidation = validateEmail(uiState.email)
        val passwordValidation = validatePassword(uiState.password)
        val confirmPasswordValidation = validateConfirmPassword(uiState.password, uiState.confirmPassword)

        uiState = uiState.copy(
            nameError = nameValidation,
            lastNameError = lastNameValidation,
            emailError = emailValidation,
            passwordError = passwordValidation,
            confirmPasswordError = confirmPasswordValidation
        )

        return nameValidation == null &&
                lastNameValidation == null &&
                emailValidation == null &&
                passwordValidation == null &&
                confirmPasswordValidation == null
    }

    private fun validateName(name: String): String? {
        return when {
            name.isBlank() -> "El nombre es requerido"
            name.trim().length < 2 -> "El nombre debe tener al menos 2 caracteres"
            name.trim().length > 50 -> "El nombre no puede tener más de 50 caracteres"
            !name.matches(Regex("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$")) -> "El nombre solo puede contener letras"
            else -> null
        }
    }

    private fun validateLastName(lastName: String): String? {
        return when {
            lastName.isBlank() -> "El apellido es requerido"
            lastName.trim().length < 2 -> "El apellido debe tener al menos 2 caracteres"
            lastName.trim().length > 50 -> "El apellido no puede tener más de 50 caracteres"
            !lastName.matches(Regex("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$")) -> "El apellido solo puede contener letras"
            else -> null
        }
    }

    private fun validateEmail(email: String): String? {
        return when {
            email.isBlank() -> "El correo es requerido"
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches() -> "Correo inválido"
            email.length > 100 -> "El correo es demasiado largo"
            else -> null
        }
    }

    private fun validatePassword(password: String): String? {
        return when {
            password.isBlank() -> "La contraseña es requerida"
            password.length < 6 -> "La contraseña debe tener al menos 6 caracteres"
            password.length > 50 -> "La contraseña no puede tener más de 50 caracteres"
            !password.matches(Regex(".*[A-Z].*")) -> "La contraseña debe contener al menos una mayúscula"
            !password.matches(Regex(".*[a-z].*")) -> "La contraseña debe contener al menos una minúscula"
            !password.matches(Regex(".*[0-9].*")) -> "La contraseña debe contener al menos un número"
            else -> null
        }
    }

    private fun validateConfirmPassword(password: String, confirmPassword: String): String? {
        return when {
            confirmPassword.isBlank() -> "Debes confirmar la contraseña"
            password != confirmPassword -> "Las contraseñas no coinciden"
            else -> null
        }
    }

    fun clearErrors() {
        uiState = uiState.copy(
            nameError = null,
            lastNameError = null,
            emailError = null,
            passwordError = null,
            confirmPasswordError = null,
            registerError = null
        )
    }
}

class RegisterViewModelFactory(private val repository: AppRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RegisterViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}