package com.example.coroutinesbasics.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.coroutinesbasics.viewmodels.MovieDetailsState

@Composable
fun MovieDetailScreen(
    movieDetailsState: MovieDetailsState
) {
    Box(
        modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
    ) {
        when (movieDetailsState) {
            is MovieDetailsState.Loading -> CircularProgressIndicator()
            is MovieDetailsState.SuccessMovieDetail -> {
                Text(
                    movieDetailsState.details.title
                )
            }

            is MovieDetailsState.ErrorMovieDetail -> {
                Text(
                    movieDetailsState.error, color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}