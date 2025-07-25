package com.example.mobox_app.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.mobox_app.data.AppRepository
import com.example.mobox_app.data.Movie
import com.example.mobox_app.data.User
import com.example.mobox_app.MoboxApp
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow


data class HomeUiState(
    val popularMovies: List<Movie> = emptyList(),
    val allMovies: List<Movie> = emptyList(),
    val currentUser: User? = null
)

class HomeViewModel(
    private val repository: AppRepository,
    private val application: MoboxApp
) : ViewModel() {


    private val _uiState = MutableStateFlow(HomeUiState(currentUser = application.currentUser))
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()


    val popularMoviesState: StateFlow<List<Movie>> =
        repository.getPopularMovies()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )


    val allMoviesState: StateFlow<List<Movie>> = repository.getAllMovies()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )


    class HomeViewModelFactory(
        private val repository: AppRepository,
        private val application: MoboxApp
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return HomeViewModel(repository, application) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}