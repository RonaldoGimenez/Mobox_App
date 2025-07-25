package com.example.mobox_app.ui.screens.moviedetail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.mobox_app.data.AppRepository
import com.example.mobox_app.data.Favorite
import com.example.mobox_app.data.Movie
import com.example.mobox_app.MoboxApp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


sealed class MovieDetailUiState {
    object Loading : MovieDetailUiState()
    data class Success(val movie: Movie) : MovieDetailUiState()
    data class Error(val message: String) : MovieDetailUiState()
}


data class MovieDetailState(
    val movieState: MovieDetailUiState = MovieDetailUiState.Loading,
    val isFavorite: Boolean = false,
    val isLoadingFavorite: Boolean = false,
    val similarMovies: List<Movie> = emptyList(),
    val showRemoveFavoriteDialog: Boolean = false
)

class MovieDetailViewModel(
    private val repository: AppRepository,
    private val movieId: Int,
    private val application: MoboxApp
) : ViewModel() {


    private val _uiState = MutableStateFlow(MovieDetailState())
    val uiState: StateFlow<MovieDetailState> = _uiState.asStateFlow()



    private val currentUserId: Int? = application.currentUser?.id


    init {

        if (currentUserId != null) {
            loadMovieDetails()
            checkIfFavorite()
            loadSimilarMovies()
        } else {
            _uiState.value = _uiState.value.copy(
                movieState = MovieDetailUiState.Error("No hay usuario logueado. Inicia sesión para ver detalles.")
            )
        }
    }


    fun loadMovieDetails() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(movieState = MovieDetailUiState.Loading)
                val movie = repository.getMovieById(movieId)
                if (movie != null) {
                    _uiState.value = _uiState.value.copy(
                        movieState = MovieDetailUiState.Success(movie)
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        movieState = MovieDetailUiState.Error("Película no encontrada")
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    movieState = MovieDetailUiState.Error("Error al cargar la película: ${e.message}")
                )
            }
        }
    }


    private fun checkIfFavorite() {
        viewModelScope.launch {
            val userId = currentUserId
            if (userId != null) {
                repository.isFavorite(userId, movieId).collect { isFavorite ->
                    _uiState.value = _uiState.value.copy(isFavorite = isFavorite)
                }
            }
        }
    }


    private fun loadSimilarMovies() {
        viewModelScope.launch {
            try {


                repository.getAllMovies().collect { allMovies ->
                    val similarMovies = allMovies
                        .filter { it.id != movieId }
                        .take(6)


                    _uiState.value = _uiState.value.copy(similarMovies = similarMovies)
                }
            } catch (e: Exception) {

                println("Error loading similar movies: ${e.message}")
            }
        }
    }


    fun onFavoriteClick() {
        val currentState = _uiState.value
        if (currentState.isFavorite) {

            _uiState.value = currentState.copy(showRemoveFavoriteDialog = true)
        } else {

            addToFavorites()
        }
    }


    fun onConfirmRemoveFavorite() {
        removeFromFavorites()
        dismissRemoveFavoriteDialog()
    }


    fun dismissRemoveFavoriteDialog() {
        _uiState.value = _uiState.value.copy(showRemoveFavoriteDialog = false)
    }


    private fun addToFavorites() {
        viewModelScope.launch {
            val userId = currentUserId
            if (userId != null) {
                try {
                    _uiState.value = _uiState.value.copy(isLoadingFavorite = true)


                    val favorite = Favorite(
                        userId = userId,
                        movieId = movieId
                    )
                    repository.addFavorite(favorite)


                    _uiState.value = _uiState.value.copy(isLoadingFavorite = false)
                } catch (e: Exception) {
                    _uiState.value = _uiState.value.copy(isLoadingFavorite = false)
                    println("Error adding to favorites: ${e.message}")

                }
            } else {
                // Handle error: User not logged in
                println("Error: No user logged in to add favorite.")

            }
        }
    }


    private fun removeFromFavorites() {
        viewModelScope.launch {
            val userId = currentUserId // AJUSTE: NUEVO - Usar el userId obtenido
            if (userId != null) { // AJUSTE: NUEVO - Validar que userId no sea null
                try {
                    _uiState.value = _uiState.value.copy(isLoadingFavorite = true)


                    repository.removeFavorite(userId, movieId)


                    _uiState.value = _uiState.value.copy(isLoadingFavorite = false)
                } catch (e: Exception) {
                    _uiState.value = _uiState.value.copy(isLoadingFavorite = false)
                    println("Error removing from favorites: ${e.message}")

                }
            } else {

                println("Error: No user logged in to remove favorite.")

            }
        }
    }
}


class MovieDetailViewModelFactory(
    private val repository: AppRepository,
    private val movieId: Int,
    private val application: MoboxApp
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MovieDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MovieDetailViewModel(repository, movieId, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}