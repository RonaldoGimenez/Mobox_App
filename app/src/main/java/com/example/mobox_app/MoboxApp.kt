package com.example.mobox_app

import android.app.Application
import com.example.mobox_app.data.AppDatabase
import com.example.mobox_app.data.AppRepository
import com.example.mobox_app.data.Movie
import com.example.mobox_app.data.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MoboxApp : Application() {
    val database by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy { AppRepository(database.appDao()) }


    var currentUser: User? = null

    override fun onCreate() {
        super.onCreate()
        addSampleMovies()
    }

    private fun addSampleMovies() {
        CoroutineScope(Dispatchers.IO).launch {
            val movieDao = database.appDao()

            if (movieDao.getAllMoviesList().isEmpty()) {
                movieDao.insertMovie(Movie(title = "Superman", description = "El último hijo de Krypton llega a la Tierra con poderes extraordinarios.", posterUrl = "https://www.themoviedb.org/t/p/w600_and_h900_bestv2/vS7N2Qd3r2jGj5J1dGg3fGj1d.jpg", genre = "Acción", isPopular = true))
                movieDao.insertMovie(Movie(title = "Batman", description = "El protector de Gotham, un justiciero oscuro.", posterUrl = "https://www.themoviedb.org/t/p/w600_and_h900_bestv2/qJ2tW6WMUDux911JEJWApGW3e1I.jpg", genre = "Acción", isPopular = false))
                movieDao.insertMovie(Movie(title = "Spider-Man", description = "Un joven con habilidades arácnidas que protege Nueva York.", posterUrl = "https://www.themoviedb.org/t/p/w600_and_h900_bestv2/rPdxQ4jH0l2G5m1yJtK5C5P7Lq.jpg", genre = "Aventura", isPopular = true))
                movieDao.insertMovie(Movie(title = "Wonder Woman", description = "Una princesa amazona que lucha por la justicia en el mundo de los hombres.", posterUrl = "https://www.themoviedb.org/t/p/w600_and_h900_bestv2/vR1mQ6Y5p3sWn5jC6j1dGg6o0h.jpg", genre = "Acción", isPopular = true))
                movieDao.insertMovie(Movie(title = "The Flash", description = "Un superhéroe con supervelocidad que viaja en el tiempo para salvar a su familia.", posterUrl = "https://www.themoviedb.org/t/p/w600_and_h900_bestv2/yYwGysL2v7X0o5N3JtK3N3O4Lq.jpg", genre = "Ciencia Ficción", isPopular = false))
                movieDao.insertMovie(Movie(title = "Aquaman", description = "El rey de Atlantis debe unirse a la superficie para salvar ambos mundos.", posterUrl = "https://www.themoviedb.org/t/p/w600_and_h900_bestv2/xXmC61JtG1z2v7X2F3G1w4H0b.jpg", genre = "Aventura", isPopular = true))
                movieDao.insertMovie(Movie(title = "Joker", description = "Un comediante fracasado se embarca en una espiral descendente de revolución y crimen.", posterUrl = "https://www.themoviedb.org/t/p/w600_and_h900_bestv2/udDclJoHjfub872PVSWTf2htzQo.jpg", genre = "Drama", isPopular = true))
                movieDao.insertMovie(Movie(title = "Inception", description = "Un ladrón que roba secretos corporativos a través de la tecnología de compartir sueños.", posterUrl = "https://www.themoviedb.org/t/p/w600_and_h900_bestv2/gC2J5c7F5F0M5N5G4E6H1K1D0w.jpg", genre = "Ciencia Ficción", isPopular = false))
                movieDao.insertMovie(Movie(title = "Interstellar", description = "Un equipo de exploradores viaja a través de un agujero de gusano para salvar a la humanidad.", posterUrl = "https://www.themoviedb.org/t/p/w600_and_h900_bestv2/g5rT2r2n3J5m4N6Q8T3C4F1A0h.jpg", genre = "Ciencia Ficción", isPopular = true))
                movieDao.insertMovie(Movie(title = "Parasite", description = "Una familia pobre se infiltra en la vida de una familia rica.", posterUrl = "https://www.themoviedb.org/t/p/w600_and_h900_bestv2/5m2W3bQpM1D6N7G0P5F3F1G0J0k.jpg", genre = "Drama", isPopular = true))
            }
        }
    }
}