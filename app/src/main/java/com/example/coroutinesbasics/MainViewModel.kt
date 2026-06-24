package com.example.coroutinesbasics

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.example.coroutinesbasics.repository.MovieUIState
import com.example.coroutinesbasics.repository.TMBDRepository
import com.example.coroutinesbasics.worker.TMBDMovieWorker
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface MainUIEvents {
    object OnOkClicked : MainUIEvents
    object OnResetClicked : MainUIEvents
    data class LoadNextPage(val page: Int) : MainUIEvents
}

class MainViewModel(
    private val workManager: WorkManager, private val tmbdRepository: TMBDRepository
) : ViewModel() {

    companion object {
        const val TMBD_MOVIE_TAG = "tmbd_movie_work_tag"
    }

    init {
        scheduleRefreshWork()
    }

    val moviesUIState: StateFlow<MovieUIState> =
        tmbdRepository.popularMovies.map { MovieUIState.SuccessMovieResponse(it) }
            .catch { MovieUIState.ErrorMovieState(it.message ?: "Error Movie global", it) }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(1000),
                initialValue = MovieUIState.Loading
            )

    val tmbdMovieWorkInfo: LiveData<List<WorkInfo>> =
        workManager.getWorkInfosByTagLiveData(TMBD_MOVIE_TAG)

    private fun scheduleRefreshWork() {
        val constraints = Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true).build()

        val tmbdMovieRequest =
            OneTimeWorkRequestBuilder<TMBDMovieWorker>().setConstraints(constraints)
                .addTag(TMBD_MOVIE_TAG).build()

        workManager.enqueue(listOf(tmbdMovieRequest))
    }

    private fun loadNexPopularMovie(currentPage: Int) {
        viewModelScope.launch {
            tmbdRepository.getPopularMoviesFirstSave(currentPage)
        }
    }

    fun handleUserEvents(event: MainUIEvents) {
        when (event) {
            is MainUIEvents.LoadNextPage -> loadNexPopularMovie(event.page)
            MainUIEvents.OnOkClicked -> {}
            MainUIEvents.OnResetClicked -> {}
        }
    }
}