package com.example.mobox_app.data

import kotlinx.coroutines.flow.Flow

class AppRepository(private val appDao: AppDao) {

    // --- Funciones para User ---
    suspend fun insertUser(user: User) {
        appDao.insertUser(user)
    }

    suspend fun getUserByEmail(email: String): User? {
        return appDao.getUserByEmail(email)
    }


    // --- Funciones para Movie ---
    suspend fun insertMovie(movie: Movie) {
        appDao.insertMovie(movie)
    }

    fun getPopularMovies(): Flow<List<Movie>> = appDao.getPopularMovies()

    fun getAllMovies(): Flow<List<Movie>> = appDao.getAllMovies()

    suspend fun getMovieById(movieId: Int): Movie? {
        return appDao.getMovieById(movieId)
    }

    fun searchMovies(query: String): Flow<List<Movie>> = appDao.searchMovies(query)


    // --- Funciones para Favorite ---
    suspend fun addFavorite(favorite: Favorite) {
        appDao.addFavorite(favorite)
    }

    suspend fun removeFavorite(userId: Int, movieId: Int) {
        appDao.removeFavorite(userId, movieId)
    }

    fun getFavoriteMovies(userId: Int): Flow<List<Movie>> = appDao.getFavoriteMovies(userId)

    fun isFavorite(userId: Int, movieId: Int): Flow<Boolean> = appDao.isFavorite(userId, movieId)
}