package com.example.coroutinesbasics.network


import com.example.coroutinesbasics.BuildConfig
import com.example.coroutinesbasics.db.MovieDetail
import com.example.coroutinesbasics.db.MovieResponse
import com.example.coroutinesbasics.utils.TmbdNetworkInterceptor
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TmdbApi {
    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("page") page: Int = 1
    ): MovieResponse

    @GET("movie/top_rated")
    suspend fun getTopRatedMovies(
        @Query("api_key") apiKey: String = BuildConfig.TMDB_API_KEY,
        @Query("page") page: Int = 1
    ): MovieResponse

    @GET("movie/{id}")
    suspend fun getMovieDetail(
        @Path("id") id: Int,
        @Query("api_key") apiKey: String = BuildConfig.TMDB_API_KEY
    ): MovieDetail

    @GET("search/movie")
    suspend fun searchMovies(
        @Query("api_key") apiKey: String = BuildConfig.TMDB_API_KEY,
        @Query("query") query: String,
        @Query("page") page: Int = 1
    ): MovieResponse

    /*@GET("tv/popular")
    suspend fun getPopularShows(
        @Query("api_key") apiKey: String = BuildConfig.TMDB_API_KEY,
        @Query("page") page: Int = 1
    ): TvResponse*/
}

fun getTMDBApiService() = tmdbApiService

private val tmdbApiService: TmdbApi by lazy {
    val okHttpClient = OkHttpClient
        .Builder()
        .addInterceptor(TmbdNetworkInterceptor())
        .build()

    val contentType = "application/json".toMediaType()
    val jsonConfig = Json {
        ignoreUnknownKeys = true // Prevents crashes if API adds new fields
        coerceInputValues = true // Fallback to defaults if values are missing/null
    }

    val retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.TMDB_BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(jsonConfig.asConverterFactory(contentType)).build()

    retrofit.create(TmdbApi::class.java)
}
