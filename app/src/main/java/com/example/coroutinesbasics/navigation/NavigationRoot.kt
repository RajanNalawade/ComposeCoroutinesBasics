package com.example.coroutinesbasics.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import com.example.coroutinesbasics.screens.HomeScreen
import com.example.coroutinesbasics.screens.MovieDetailScreen
import com.example.coroutinesbasics.viewmodels.MainViewModel
import com.example.coroutinesbasics.viewmodels.MovieDetailViewModel
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun NavigationRoot(modifier: Modifier = Modifier) {
    val backStack = rememberNavBackStack(
        configuration = SavedStateConfiguration {
            serializersModule = SerializersModule {
                polymorphic(
                    baseClass = NavKey::class,
                ) {
                    subclass(Route.HomeScreen::class, Route.HomeScreen.serializer())
                    subclass(
                        Route.MovieDetailScreen::class, Route.MovieDetailScreen.serializer()
                    )
                }
            }
        }, elements = arrayOf(Route.HomeScreen)
    )

    NavDisplay(
        modifier = modifier, backStack = backStack, entryDecorators = listOf(
        rememberSaveableStateHolderNavEntryDecorator(),
        rememberViewModelStoreNavEntryDecorator()
    ), onBack = { backStack.removeLastOrNull() }, entryProvider = { key ->
        when (key) {
            is Route.HomeScreen -> {
                NavEntry(key) {
                    val viewModel: MainViewModel = koinViewModel()
                    val movieUIState by viewModel.moviesUIState.collectAsStateWithLifecycle()

                    HomeScreen(
                        movieUIState = movieUIState,
                        onEvent = { event -> viewModel.handleUserEvents(event) },
                        onItemClicked = { itemId ->
                            backStack.add(Route.MovieDetailScreen(id = itemId))
                        })
                }
            }

            is Route.MovieDetailScreen -> {
                NavEntry(key) {
                    val viewModel: MovieDetailViewModel = koinViewModel { parametersOf(key.id) }
                    val movieDetailState by viewModel.moviesDetailState.collectAsStateWithLifecycle()
                    MovieDetailScreen(
                        movieDetailState
                    )
                }
            }

            else -> error("Unknowe Navkey: $key")
        }
    })
}