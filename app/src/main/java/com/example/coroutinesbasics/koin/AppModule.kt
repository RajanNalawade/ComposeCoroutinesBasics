package com.example.coroutinesbasics.koin

import androidx.work.WorkManager
import com.example.coroutinesbasics.db.getTMBDMovieDatabase
import com.example.coroutinesbasics.network.getTMDBApiService
import com.example.coroutinesbasics.repository.TMBDRepository
import com.example.coroutinesbasics.viewmodels.MainViewModel
import com.example.coroutinesbasics.viewmodels.MovieDetailViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

@OptIn(KoinExperimentalAPI::class)
val appModule = module {
    single { getTMDBApiService() }
    single { getTMBDMovieDatabase(androidContext()).tmbdMovieDao }
    single { TMBDRepository(get(), get()) }
    single { WorkManager.getInstance(androidApplication()) }
    viewModel { MainViewModel(get(), get()) }
    viewModel { (movieId: Int) -> MovieDetailViewModel(movieId, get()) }
}
