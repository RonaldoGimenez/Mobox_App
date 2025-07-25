package com.example.mobox_app.ui.screens.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.mobox_app.data.AppRepository
import com.example.mobox_app.data.Movie
import com.example.mobox_app.MoboxApp // AJUSTE: NUEVO - Importar MoboxApp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Estados para la UI de favoritos
sealed class FavoritesUiState {
    object Loading : FavoritesUiState()
    object Empty : FavoritesUiState()
    data class Success(val movies: List<Movie>) : FavoritesUiState()
    data class Error(val message: String) : FavoritesUiState()
}

class FavoritesViewModel(
    private val repository: AppRepository,
    private val application: MoboxApp // AJUSTE: NUEVO - Añadir MoboxApp como dependencia
) : ViewModel() {


    private val _uiState =
        MutableStateFlow<FavoritesUiState>(FavoritesUiState.Loading)
    val uiState: StateFlow<FavoritesUiState> = _uiState.asStateFlow()

    // AJUSTE: MODIFICADO - Obtener el userId dinámicamente
    private val currentUserId: Int? = application.currentUser?.id


    init {
        // AJUSTE: MODIFICADO - Solo cargar favoritos si hay un usuario logueado
        if (currentUserId != null) {
            loadFavoriteMovies()
        } else {
            _uiState.value = FavoritesUiState.Error("No hay usuario logueado para ver favoritos.")
        }
    }


    fun loadFavoriteMovies() { // AJUSTE: MODIFICADO - Hacer pública para el reintento si es necesario
        viewModelScope.launch {
            val userId = currentUserId // AJUSTE: NUEVO - Usar el userId obtenido
            if (userId != null) { // AJUSTE: NUEVO - Validar que userId no sea null
                try {
                    _uiState.value = FavoritesUiState.Loading


                    repository.getFavoriteMovies(userId).collect { movies ->
                        _uiState.value = if (movies.isEmpty()) {
                            FavoritesUiState.Empty
                        } else {
                            FavoritesUiState.Success(movies)
                        }
                    }
                } catch (e: Exception) {
                    _uiState.value = FavoritesUiState.Error("Error al cargar favoritos: ${e.message}")
                }
            } else {
                _uiState.value = FavoritesUiState.Error("Error: No se pudo obtener el ID del usuario.")
            }
        }
    }


    fun refreshFavorites() {
        loadFavoriteMovies()
    }


    fun removeFavorite(movieId: Int) {
        viewModelScope.launch {
            val userId = currentUserId // AJUSTE: NUEVO - Usar el userId obtenido
            if (userId != null) { // AJUSTE: NUEVO - Validar que userId no sea null
                try {
                    repository.removeFavorite(userId, movieId)
                    // No necesitamos actualizar manualmente el estado porque
                    // el Flow se actualiza automáticamente al ser un Flow
                } catch (e: Exception) {
                    // En caso de error, podrías mostrar un snackbar o mensaje
                    println("Error removing favorite: ${e.message}")
                }
            } else {
                println("Error: No user logged in to remove favorite.")
            }
        }
    }
}


class FavoritesViewModelFactory(
    private val repository: AppRepository,
    private val application: MoboxApp
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FavoritesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FavoritesViewModel(repository, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}