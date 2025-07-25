package com.example.mobox_app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mobox_app.ui.screens.home.HomeScreen
import com.example.mobox_app.ui.screens.login.LoginScreen
import com.example.mobox_app.ui.screens.register.RegisterScreen
import com.example.mobox_app.ui.screens.search.SearchScreen
import com.example.mobox_app.ui.screens.moviedetail.MovieDetailScreen
import com.example.mobox_app.ui.screens.favorites.FavoritesScreen

object AppRoutes {
    const val LOGIN_SCREEN = "login"
    const val REGISTER_SCREEN = "register"
    const val HOME_SCREEN = "home"
    const val SEARCH_SCREEN = "search"
    const val MOVIE_DETAIL_SCREEN = "movie_detail"
    const val FAVORITES_SCREEN = "favorites"
}

@Composable
fun AppNavigationGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AppRoutes.LOGIN_SCREEN
    ) {
        composable(AppRoutes.LOGIN_SCREEN) {
            LoginScreen(
                onRegisterClick = {
                    navController.navigate(AppRoutes.REGISTER_SCREEN)
                },
                onLoginSuccess = {
                    navController.navigate(AppRoutes.HOME_SCREEN) {
                        // Limpiar el stack para que no pueda volver al login
                        popUpTo(AppRoutes.LOGIN_SCREEN) { inclusive = true }
                    }
                }
            )
        }

        composable(AppRoutes.REGISTER_SCREEN) {
            RegisterScreen(
                onLoginClick = {
                    navController.popBackStack()
                },
                onRegisterSuccess = {
                    navController.navigate(AppRoutes.LOGIN_SCREEN) {
                        popUpTo(AppRoutes.REGISTER_SCREEN) { inclusive = true }
                    }
                }
            )
        }

        composable(AppRoutes.HOME_SCREEN) {
            HomeScreen(
                onSearchClick = {
                    navController.navigate(AppRoutes.SEARCH_SCREEN)
                },
                onMovieClick = { movieId ->
                    navController.navigate("${AppRoutes.MOVIE_DETAIL_SCREEN}/$movieId")
                },
                onFavoritesClick = {
                    navController.navigate(AppRoutes.FAVORITES_SCREEN)
                },
                onLogoutClick = {
                    navController.navigate(AppRoutes.LOGIN_SCREEN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(AppRoutes.SEARCH_SCREEN) {
            SearchScreen(
                onMovieClick = { movieId ->
                    navController.navigate("${AppRoutes.MOVIE_DETAIL_SCREEN}/$movieId")
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable("${AppRoutes.MOVIE_DETAIL_SCREEN}/{movieId}") { backStackEntry ->
            val movieId = backStackEntry.arguments?.getString("movieId")?.toIntOrNull() ?: 0
            MovieDetailScreen(
                movieId = movieId,
                onBackClick = { navController.popBackStack() },
                onMovieClick = { newMovieId ->
                    navController.navigate("${AppRoutes.MOVIE_DETAIL_SCREEN}/$newMovieId")
                }
            )
        }

        composable(AppRoutes.FAVORITES_SCREEN) {
            FavoritesScreen(
                onMovieClick = { movieId ->
                    navController.navigate("${AppRoutes.MOVIE_DETAIL_SCREEN}/$movieId")
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}