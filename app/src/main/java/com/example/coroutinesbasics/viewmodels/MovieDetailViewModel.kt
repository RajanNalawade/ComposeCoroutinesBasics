package com.example.coroutinesbasics.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coroutinesbasics.db.Movie
import com.example.coroutinesbasics.repository.TMBDRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

sealed interface MovieDetailsState {
    object Loading : MovieDetailsState
    data class ErrorMovieDetail(val error: String, val cause: Throwable) : MovieDetailsState
    data class SuccessMovieDetail(val details: Movie) : MovieDetailsState
}

class MovieDetailViewModel(private val movieId: Int, tmbdRepository: TMBDRepository) : ViewModel() {

    val moviesDetailState: StateFlow<MovieDetailsState> =
        tmbdRepository.popularMovies.map { response ->
            val movie: Movie = response.flatMap { it.movies }.first { it.id == movieId }
            MovieDetailsState.SuccessMovieDetail(movie)
        }
            .catch { MovieDetailsState.ErrorMovieDetail(it.message ?: "Error Movie global", it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(1000),
                initialValue = MovieDetailsState.Loading
            )

    init {
        Log.d("Nav3", "Movie Details VM initializes for id: $movieId")
    }

    override fun onCleared() {
        Log.d("Nav3", "Movie Details VM cleared for id: $movieId")
        super.onCleared()
    }
}