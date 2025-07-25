// AppDao.kt
package com.example.mobox_app.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    // --- Funciones para User ---
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertUser(user: User)

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): User?

    // --- Funciones para Movie ---
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMovie(movie: Movie)

    @Query("SELECT * FROM movies WHERE isPopular = 1")
    fun getPopularMovies(): Flow<List<Movie>>

    @Query("SELECT * FROM movies")
    fun getAllMovies(): Flow<List<Movie>>

    @Query("SELECT * FROM movies WHERE id = :movieId LIMIT 1")
    suspend fun getMovieById(movieId: Int): Movie?

    @Query("SELECT * FROM movies WHERE title LIKE '%' || :query || '%'")
    fun searchMovies(query: String): Flow<List<Movie>>

    // --- Funciones para Favorite ---
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addFavorite(favorite: Favorite)

    @Query("DELETE FROM favorites WHERE userId = :userId AND movieId = :movieId")
    suspend fun removeFavorite(userId: Int, movieId: Int)


    @RewriteQueriesToDropUnusedColumns //
    @Query("SELECT * FROM movies INNER JOIN favorites ON movies.id = favorites.movieId WHERE favorites.userId = :userId")
    fun getFavoriteMovies(userId: Int): Flow<List<Movie>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE userId = :userId AND movieId = :movieId)")
    fun isFavorite(userId: Int, movieId: Int): Flow<Boolean>

    @Query("SELECT * FROM movies")
    suspend fun getAllMoviesList(): List<Movie> // Función para la verificación
}