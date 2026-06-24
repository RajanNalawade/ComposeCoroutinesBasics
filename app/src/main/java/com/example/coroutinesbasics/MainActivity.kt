package com.example.coroutinesbasics

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import coil3.compose.LocalPlatformContext
import coil3.compose.rememberAsyncImagePainter
import coil3.compose.rememberConstraintsSizeResolver
import coil3.request.ImageRequest
import coil3.request.allowHardware
import coil3.request.crossfade
import com.example.coroutinesbasics.db.Movie
import com.example.coroutinesbasics.db.MovieResponse
import com.example.coroutinesbasics.repository.MovieUIState
import com.example.coroutinesbasics.ui.theme.AppTheme
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val windowSizeClass = calculateWindowSizeClass(this)
            AppTheme {
                BasicsAppLayout(windowSizeClass)
            }
        }
    }
}

@Composable
fun BasicsAppLayout(
    windowSize: WindowSizeClass? = null, viewModel: MainViewModel = koinViewModel()
) {
    val movieUIState by viewModel.moviesUIState.collectAsState()

    BasicsAppLayoutContent(
        windowSize = windowSize, movieUIState = movieUIState,
        // Send any event straight to the ViewModel's event handler function
        onEvent = { event -> viewModel.handleUserEvents(event) })
}

@Composable
fun BasicsAppLayoutContent(
    windowSize: WindowSizeClass? = null, movieUIState: MovieUIState, onEvent: (MainUIEvents) -> Unit
) {
    when (windowSize?.widthSizeClass) {
        WindowWidthSizeClass.Expanded -> {
            GreetingsAppLandscape(
                movieUIState = movieUIState, onEvent = onEvent
            )
        }

        else -> GreetingsAppPortrait(
            movieUIState = movieUIState, onEvent = onEvent
        )
    }
}

@Composable
fun GreetingsAppLandscape(
    modifier: Modifier = Modifier, movieUIState: MovieUIState, onEvent: (MainUIEvents) -> Unit
) {
    AppTheme {
        Scaffold(modifier) { innerPadding ->
            Greeting(
                movieUIState = movieUIState,
                modifier = Modifier.padding(innerPadding),
                onEvent = onEvent
            )
        }
    }
}

@Composable
fun GreetingsAppPortrait(
    modifier: Modifier = Modifier, movieUIState: MovieUIState, onEvent: (MainUIEvents) -> Unit
) {
    AppTheme {
        Scaffold(modifier) { innerPadding ->
            Greeting(
                movieUIState = movieUIState,
                modifier = Modifier.padding(innerPadding),
                onEvent = onEvent
            )
        }
    }
}

@Composable
fun Greeting(
    movieUIState: MovieUIState, modifier: Modifier = Modifier, onEvent: (MainUIEvents) -> Unit
) {
    Box(
        modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            when (movieUIState) {
                is MovieUIState.Loading -> CircularProgressIndicator()
                is MovieUIState.SuccessMovieResponse -> {
                    MovieLazyRow(
                        movies = movieUIState.response, modifier = modifier, onEvent = onEvent
                    )
                }

                is MovieUIState.ErrorMovieState -> Text(
                    movieUIState.error, color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun MovieLazyRow(
    movies: List<MovieResponse>,
    onEvent: (MainUIEvents) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    // 1. CACHE THE LIST: Prevents recreating a brand-new list on every single scroll frame
    val flatMovieList = remember(movies) {
        movies.flatMap { it.movies }
    }

    val lastElement = movies.lastOrNull()

    // 2. STABLE PAGINATION TRIGGER: Keyed to flatMovieList.size to prevent extra recompositions
    val shouldLoadMore by remember(flatMovieList.size) {
        derivedStateOf {
            val lastVisibleItem =
                listState.layoutInfo.visibleItemsInfo.lastOrNull() ?: return@derivedStateOf false
            val totalItemsCount = listState.layoutInfo.totalItemsCount

            lastVisibleItem.index >= totalItemsCount - 3
        }
    }

    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore && lastElement != null && lastElement.page < lastElement.totalPages) {
            // Note: Use page + 1 to fetch the actual NEXT page
            onEvent(MainUIEvents.LoadNextPage(lastElement.page + 1))
        }
    }

    LazyRow(
        state = listState,
        modifier = modifier
            .height(240.dp)
            .fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 3. ADD KEYS & CONTENT TYPE: Allows To Compose to reuse layout nodes without flickering
        items(
            items = flatMovieList,
            key = { movie -> movie.id }, // Crucial: Stops images from flashing/reloading
            contentType = { "movie_item" } // Groups items for ideal recyclerview recycling
        ) { singleMovie ->
            ImageRowItem(movie = singleMovie)
        }

        if (lastElement != null && lastElement.page < lastElement.totalPages) {
            item(key = "pagination_loader", contentType = "loader") {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(60.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                }
            }
        }
    }
}


@Composable
fun ImageRowItem(movie: Movie, modifier: Modifier = Modifier) {
    val sizeResolver = rememberConstraintsSizeResolver()
    val painter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalPlatformContext.current)
            .data(movie.backdropUrl)
            .crossfade(true)
            .allowHardware(true)
            .size(sizeResolver).build()
    )

    Column(
        modifier = modifier
            .wrapContentWidth()
            .wrapContentHeight(),
        horizontalAlignment = Alignment.Start
    ) {
        // 1. The Image (keeps its exact current behavior)
        Image(
            painter = painter,
            contentDescription = "Movie backdrop",
            modifier = Modifier
                .height(180.dp)
                .then(sizeResolver)
        )

        Spacer(modifier = Modifier.height(4.dp))

        // 2. The Movie Title (TextView equivalent)
        Text(
            text = movie.title,
            style = MaterialTheme.typography.titleMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.width(120.dp) // Match this roughly to your target image width
        )

        Spacer(modifier = Modifier.height(2.dp))

        // 3. The Ratings Row
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "Rating Star",
                tint = Color(0xFFFFC107), // Gold/Yellow color
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = movie.rating.toString(),
                style = MaterialTheme.typography.titleSmall,
                color = Color.Gray
            )
        }
    }
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    val fakeSize = DpSize(360.dp, 800.dp)
    val fakeWindowSizeClass = WindowSizeClass.calculateFromSize(fakeSize)
    AppTheme {
        BasicsAppLayoutContent(
            windowSize = fakeWindowSizeClass, movieUIState = MovieUIState.SuccessMovieResponse(
                response = listOf(
                    MovieResponse(
                        page = 1, movies = listOf(
                            Movie(
                                id = 1392469,
                                title = "Cocktail 2",
                                description = "After a decade together, Diya and Kunal's relationship is shaken when Ally, an old friend, re-enters their lives. What begins as a plan between two women spirals into chaos, triggering hilarious, emotional rollercoaster none of them saw coming.",
                                rating = 5.7,
                                voteCount = 7,
                                posterPath = "/sJB5guRMyS6jLo0cLsY8cYBAjzQ.jpg",
                                backdropPath = "/6tROOVmV5vRymO2g52aR8kWlfoT.jpg",
                                releaseDate = "2026-06-19",
                                genreIds = listOf(10749, 35),
                                createdAt = 1782296597950
                            )
                        ), totalPages = 40, totalResults = 160
                    ),
                )
            ), onEvent = {})
    }
}