package com.example.mobox_app.ui.screens.moviedetail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.mobox_app.MoboxApp
import com.example.mobox_app.data.Movie
import com.example.mobox_app.ui.components.MoviePosterCard
import com.example.mobox_app.ui.theme.GradientEnd
import com.example.mobox_app.ui.theme.GradientStart
import com.example.mobox_app.ui.theme.Skeleton
import com.example.mobox_app.ui.theme.Stroke

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDetailScreen(
    movieId: Int,
    onBackClick: () -> Unit,
    onMovieClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val application = LocalContext.current.applicationContext as MoboxApp
    val viewModel: MovieDetailViewModel = viewModel(
        factory = MovieDetailViewModelFactory(
            repository = application.repository,
            movieId = movieId,
            application = application // AJUSTE: NUEVO - PASAR LA INSTANCIA DE APPLICATION
        )
    )


    val uiState by viewModel.uiState.collectAsState()


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Película") },
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
        when (val movieState = uiState.movieState) {
            is MovieDetailUiState.Loading -> {
                LoadingState(modifier = Modifier.padding(paddingValues))
            }
            is MovieDetailUiState.Success -> {
                MovieDetailContent(
                    movie = movieState.movie,
                    isFavorite = uiState.isFavorite,
                    isLoadingFavorite = uiState.isLoadingFavorite,
                    similarMovies = uiState.similarMovies,
                    onFavoriteClick = viewModel::onFavoriteClick,
                    onMovieClick = onMovieClick,
                    modifier = Modifier.padding(paddingValues)
                )
            }
            is MovieDetailUiState.Error -> {
                // AJUSTE: MODIFICADO - Implementar retry
                ErrorState(
                    message = movieState.message,
                    onRetryClick = { viewModel.loadMovieDetails() }, // Llama al método de carga inicial del ViewModel
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }


        // Diálogo de confirmación para quitar de favoritos
        if (uiState.showRemoveFavoriteDialog) {
            RemoveFavoriteDialog(
                onConfirm = viewModel::onConfirmRemoveFavorite,
                onDismiss = viewModel::dismissRemoveFavoriteDialog
            )
        }
    }
}

@Composable
private fun MovieDetailContent(
    movie: Movie,
    isFavorite: Boolean,
    isLoadingFavorite: Boolean,
    similarMovies: List<Movie>,
    onFavoriteClick: () -> Unit,
    onMovieClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Header con poster y información básica
        item {
            MovieHeader(
                movie = movie,
                isFavorite = isFavorite,
                isLoadingFavorite = isLoadingFavorite,
                onFavoriteClick = onFavoriteClick
            )
        }


        // Descripción
        item {
            MovieDescription(movie = movie)
        }


        // Películas similares
        if (similarMovies.isNotEmpty()) {
            item {
                SimilarMoviesSection(
                    movies = similarMovies,
                    onMovieClick = onMovieClick
                )
            }
        }
    }
}

@Composable
private fun MovieHeader(
    movie: Movie,
    isFavorite: Boolean,
    isLoadingFavorite: Boolean,
    onFavoriteClick: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Poster
        Card(
            modifier = Modifier
                .width(150.dp)
                .height(225.dp)
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(movie.posterUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = movie.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }


        // Información de la película
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "${movie.title} (2025)", // Placeholder para el año
                style = MaterialTheme.typography.titleLarge.copy(fontWeight =
                    FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )


            Text(
                text = "2h 36m", // Placeholder para duración
                style = MaterialTheme.typography.bodyMedium,
                color = Skeleton
            )


            // Géneros
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                GenreChip(text = movie.genre)
                GenreChip(text = "Ciencia Ficción") // Placeholder
            }

            // Calificación
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Calificación",
                    tint = Color.Yellow,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "8.1/10", // Placeholder
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight =
                        FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }


            // Botón de favoritos
            Button(
                onClick = onFavoriteClick,
                enabled = !isLoadingFavorite,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isFavorite) GradientEnd else
                        MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isLoadingFavorite) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else
                            Icons.Default.FavoriteBorder,
                        contentDescription = if (isFavorite) "Quitar de favoritos" else "Agregar a favoritos",
                                modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isFavorite) "Quitar de Favoritos" else "Agregar a Favoritos"
                    )
                }
            }
        }
    }
}

@Composable
private fun GenreChip(text: String) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.clip(RoundedCornerShape(16.dp))
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

@Composable
private fun MovieDescription(movie: Movie) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Sinopsis",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight =
                FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = movie.description,
            style = MaterialTheme.typography.bodyMedium,
            color = Stroke,
            lineHeight = MaterialTheme.typography.bodyMedium.lineHeight
        )
    }
}

@Composable
private fun SimilarMoviesSection(
    movies: List<Movie>,
    onMovieClick: (Int) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Películas similares",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight =
                FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )


        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            items(movies) { movie ->
                MoviePosterCard(
                    movie = movie,

                    onMovieClick = onMovieClick
                )
            }
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
                text = "Cargando película...",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "!", // Puedes cambiar este por un icono de error
                style = MaterialTheme.typography.headlineLarge
            )
            Text(
                text = "Error al cargar la película",
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

@Composable
private fun RemoveFavoriteDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Quitar de Favoritos")
        },
        text = {
            Text("¿Deseas Quitar de Favoritos?")
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Si")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("No")
            }
        }
    )
}