package com.example.mobox_app.ui.screens.search

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.mobox_app.data.AppRepository
import com.example.mobox_app.data.Movie
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

// Estados para la UI de búsqueda
sealed class SearchUiState {
    object Loading : SearchUiState()
    object Empty : SearchUiState()
    data class Success(val movies: List<Movie>) : SearchUiState()
    data class Error(val message: String) : SearchUiState()
    object NoResults : SearchUiState()
}

// Data class para el estado de búsqueda
data class SearchState(
    val searchQuery: String = "",
    val searchResults: SearchUiState = SearchUiState.Empty,
    val isSearching: Boolean = false
)

class SearchViewModel(private val repository: AppRepository) : ViewModel() {

    var searchState by mutableStateOf(SearchState())
        private set

    // Flow para el texto de búsqueda
    private val searchQueryFlow = MutableStateFlow("")

    init {
        // Configuramos la búsqueda reactiva con debounce
        setupSearch()
    }

    @OptIn(FlowPreview::class)
    private fun setupSearch() {
        viewModelScope.launch {
            searchQueryFlow
                .debounce(300) // Espera 300ms después del último cambio
                .distinctUntilChanged() // Solo busca si el query cambió
                .collect { query ->
                    if (query.isBlank()) {
                        searchState = searchState.copy(
                            searchResults = SearchUiState.Empty,
                            isSearching = false
                        )
                    } else {
                        performSearch(query)
                    }
                }
        }
    }

    fun onSearchQueryChange(query: String) {
        searchState = searchState.copy(
            searchQuery = query,
            isSearching = query.isNotBlank()
        )
        searchQueryFlow.value = query
    }

    private fun performSearch(query: String) {
        viewModelScope.launch {
            try {
                searchState = searchState.copy(searchResults = SearchUiState.Loading)

                repository.searchMovies(query).collect { movies ->
                    searchState = searchState.copy(
                        searchResults = if (movies.isEmpty()) {
                            SearchUiState.NoResults
                        } else {
                            SearchUiState.Success(movies)
                        },
                        isSearching = false
                    )
                }
            } catch (e: Exception) {
                searchState = searchState.copy(
                    searchResults = SearchUiState.Error("Error al buscar películas: ${e.message}"),
                    isSearching = false
                )
            }
        }
    }

    fun clearSearch() {
        searchState = SearchState()
        searchQueryFlow.value = ""
    }
}

// Factory para el ViewModel
class SearchViewModelFactory(private val repository: AppRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SearchViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}