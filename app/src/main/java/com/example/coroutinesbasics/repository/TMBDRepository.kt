package com.example.coroutinesbasics.repository

import com.example.coroutinesbasics.MainUIEvents
import com.example.coroutinesbasics.db.MovieResponse
import com.example.coroutinesbasics.db.TMBDMovieDao
import com.example.coroutinesbasics.network.TmdbApi
import kotlinx.coroutines.flow.map

sealed interface MovieUIState {
    object Loading : MovieUIState
    data class ErrorMovieState(val error: String, val cause: Throwable) : MovieUIState
    data class SuccessMovieResponse(val response: List<MovieResponse>) : MovieUIState
}

class TMBDRepository(val tmdbApi: TmdbApi, val tmbdMovieDao: TMBDMovieDao) {

    val popularMovies = tmbdMovieDao.popularMovies
    /*fun getPopularMovies() = flow {
        emit(MovieUIState.Loading)
        val response = tmdbApi.getPopularMovies()
        emit(MovieUIState.SuccessMovieResponse(response))
    }.flowOn(Dispatchers.IO)*/

    suspend fun getPopularMoviesFirstSave(page: Int = 1) {
        try {
            val result = tmdbApi.getPopularMovies(page = page)
            tmbdMovieDao.insertMovieResponse(result)
        } catch (error: Throwable) {
            TMBDNetworkError("error on getPopularMoviesFirstSave", error)
        }
    }
}

class TMBDNetworkError(error: String, cause: Throwable) : Throwable(error, cause)