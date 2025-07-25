package com.example.mobox_app.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mobox_app.MoboxApp
import com.example.mobox_app.ui.components.HomeTopBar
import com.example.mobox_app.ui.components.MovieListRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onSearchClick: () -> Unit = {},
    onMovieClick: (Int) -> Unit = {},
    onFavoritesClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {}
) {
    val application = LocalContext.current.applicationContext as MoboxApp
    val viewModel: HomeViewModel = viewModel(
        factory = HomeViewModel.HomeViewModelFactory(
            repository = application.repository,
            application = application
        )
    )

    val popularMovies by viewModel.popularMoviesState.collectAsState()
    val allMovies by viewModel.allMoviesState.collectAsState()
    val homeUiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            val userName = homeUiState.currentUser?.name ?: "Usuario"
            val userLastName = homeUiState.currentUser?.lastName ?: ""
            val displayUserName = "$userName $userLastName"
            val userInitials = if (userName.isNotEmpty() && userLastName.isNotEmpty()) {
                "${userName.first()}${userLastName.first()}".uppercase()
            } else if (userName.isNotEmpty()) {
                userName.first().toString().uppercase()
            } else {
                "?"
            }

            HomeTopBar(
                userName = displayUserName,
                userInitials = userInitials,
                onLogoutClick = {
                    application.currentUser = null
                    onLogoutClick()
                }
            )
        },
        bottomBar = {
            BottomNavigationBar(
                onHomeClick = { /* Ya estamos en Home */ },
                onSearchClick = onSearchClick,
                onFavoritesClick = onFavoritesClick
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            if (popularMovies.isNotEmpty()) {
                item {
                    MovieListRow(
                        title = "Top 10 Semanal",
                        movies = popularMovies,
                        onMovieClick = onMovieClick
                    )
                }
            }

            if (allMovies.isNotEmpty()) {
                item {
                    MovieListRow(
                        title = "Todas las películas",
                        movies = allMovies,
                        onMovieClick = onMovieClick
                    )
                }
            }

            if (popularMovies.isEmpty() && allMovies.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = androidx.compose.ui.Alignment.Center
                    ) {
                        Text(
                            text = "No hay películas disponibles",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BottomNavigationBar(
    onHomeClick: () -> Unit,
    onSearchClick: () -> Unit,
    onFavoritesClick: () -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Home"
                )
            },
            label = { Text("Home") },
            selected = true,
            onClick = onHomeClick
        )
        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Buscar"
                )
            },
            label = { Text("Buscar") },
            selected = false,
            onClick = onSearchClick
        )
        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Favoritos"
                )
            },
            label = { Text("Favoritos") },
            selected = false,
            onClick = onFavoritesClick
        )
    }
}