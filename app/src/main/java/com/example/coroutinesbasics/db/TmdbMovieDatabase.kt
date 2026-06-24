package com.example.coroutinesbasics.db

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Entity(tableName = "movies")
@Serializable
data class Movie(
    @PrimaryKey @SerialName("id") val id: Int,
    @SerialName("title") val title: String,
    @SerialName("overview") val description: String,
    @SerialName("vote_average") val rating: Double,
    @SerialName("vote_count") val voteCount: Int,
    @SerialName("poster_path") val posterPath: String?,
    @SerialName("backdrop_path") val backdropPath: String?,
    @SerialName("release_date") val releaseDate: String,
    @SerialName("genre_ids") val genreIds: List<Int>,
    @SerialName("created_at") val createdAt: Long = System.currentTimeMillis()
) {
    @Ignore
    val posterUrl = posterPath?.let {
        "https://image.tmdb.org/t/p/w500$it"
    }

    @Ignore
    val backdropUrl = backdropPath?.let {
        "https://image.tmdb.org/t/p/w1280$it"
    }
}

@Entity
@Serializable
data class MovieResponse(
    @PrimaryKey @SerialName("page") val page: Int,
    @SerialName("results") val movies: List<Movie>,
    @SerialName("total_pages") val totalPages: Int,
    @SerialName("total_results") val totalResults: Int,
    @SerialName("created_at") val createdAt: Long = System.currentTimeMillis()
)

@Entity
@Serializable
data class MovieDetail(
    @PrimaryKey @SerialName("id") val id: Int,
    @SerialName("title") val title: String,
    @SerialName("overview") val description: String,
    @SerialName("vote_average") val rating: Double,
    @SerialName("runtime") val runtime: Int,
    @SerialName("genres") val genres: List<Genre>,
    @SerialName("poster_path") val posterPath: String?,
    @SerialName("backdrop_path") val backdropPath: String?,
    @SerialName("tagline") val tagline: String,
    @SerialName("created_at") val createdAt: Long = System.currentTimeMillis()
) {
    @Ignore
    val posterUrl = posterPath?.let { "https://image.tmdb.org/t/p/w500$it" }

    @Ignore
    val backdropUrl = backdropPath?.let { "https://image.tmdb.org/t/p/w1280$it" }
}

@Entity
@Serializable
data class Genre(
    @PrimaryKey @SerialName("id") val id: Int,
    @SerialName("name") val name: String,
    @SerialName("created_at") val createdAt: Long = System.currentTimeMillis()
)

@Dao
interface TMBDMovieDao {

    /*@Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovies(movies: List<Movie>)*/

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovieResponse(movies: MovieResponse)

    /*@Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovieDetails(details: MovieDetail)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGenre(genres: List<Genre>)*/

    @get:Query("select * from MovieResponse")
    val popularMovies: Flow<List<MovieResponse>>

}

@TypeConverters(MovieTypeConverter::class)
@Database(entities = [MovieResponse::class], version = 1, exportSchema = false)
abstract class TmdbMovieDatabase : RoomDatabase() {
    abstract val tmbdMovieDao: TMBDMovieDao
}

private lateinit var TMBD_MOVIE_DB_INSTANCE: TmdbMovieDatabase

fun getTMBDMovieDatabase(context: Context): TmdbMovieDatabase {
    synchronized(TmdbMovieDatabase::class) {
        if (!::TMBD_MOVIE_DB_INSTANCE.isInitialized) {
            TMBD_MOVIE_DB_INSTANCE = Room.databaseBuilder(
                context.applicationContext, TmdbMovieDatabase::class.java, "tmbd_movie_db"
            ).fallbackToDestructiveMigration(false).build()
        }
    }
    return TMBD_MOVIE_DB_INSTANCE
}

