package com.example.coroutinesbasics.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface Route: NavKey {

    @Serializable
    data object HomeScreen: Route, NavKey

    @Serializable
    data class MovieDetailScreen(val id: Int): Route, NavKey

}
