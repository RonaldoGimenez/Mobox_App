package com.example.mobox_app.ui.screens.favorites

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mobox_app.MoboxApp
import com.example.mobox_app.ui.components.MovieListItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    onMovieClick: (Int) -> Unit,
    onBackClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val application = LocalContext.current.applicationContext as MoboxApp
    val viewModel: FavoritesViewModel = viewModel(
        factory = FavoritesViewModelFactory(
            repository = application.repository,
            application = application // AJUSTE: NUEVO - PASAR LA INSTANCIA DE APPLICATION
        )
    )


    val uiState by viewModel.uiState.collectAsState()


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Favoritos") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        when (val currentState = uiState) {
            is FavoritesUiState.Loading -> {
                LoadingState(modifier = Modifier.padding(paddingValues))
            }
            is FavoritesUiState.Empty -> {
                EmptyFavoritesState(modifier = Modifier.padding(paddingValues))
            }
            is FavoritesUiState.Success -> {
                FavoritesContent(
                    movies = currentState.movies,
                    onMovieClick = onMovieClick,
                    modifier = Modifier.padding(paddingValues)
                )
            }
            is FavoritesUiState.Error -> {
                ErrorState(
                    message = currentState.message,
                    onRetryClick = { viewModel.refreshFavorites() },
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
}

@Composable
private fun FavoritesContent(
    movies: List<com.example.mobox_app.data.Movie>,
    onMovieClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Mis películas",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight =
                        FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )


                // Badge con el número de favoritos
                Surface(
                    color = MaterialTheme.colorScheme.primary,
                    shape = androidx.compose.foundation.shape.CircleShape
                ) {
                    Text(
                        text = "${movies.size}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }


        items(movies) { movie ->
            FavoriteMovieItem(
                movie = movie,
                onMovieClick = onMovieClick
            )
        }
    }
}

@Composable
private fun FavoriteMovieItem(
    movie: com.example.mobox_app.data.Movie,
    onMovieClick: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            MovieListItem(
                movie = movie,
                onMovieClick = onMovieClick,
                modifier = Modifier.weight(1f)
            )


            // Indicador de favorito
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = "Mi Favorito",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .size(24.dp)
            )
        }
    }
}

@Composable
private fun LoadingState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator()
            Text(
                text = "Cargando favoritos...",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
private fun EmptyFavoritesState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.FavoriteBorder,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            Text(
                text = "Empieza a Agregar tus películas Favoritas",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                textAlign = TextAlign.Center
            )
            Text(
                text = "Explora el catálogo y marca tus películas favoritas para verlas aquí",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun ErrorState(
    message: String,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Text(
                text = "!", // Puedes cambiar este por un icono de error
                style = MaterialTheme.typography.headlineLarge
            )
            Text(
                text = "Error al cargar favoritos",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.error
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                textAlign = TextAlign.Center
            )
            Button(onClick = onRetryClick) {
                Text("Reintentar")
            }
        }
    }
}