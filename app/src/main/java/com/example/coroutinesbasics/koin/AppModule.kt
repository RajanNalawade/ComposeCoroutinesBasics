package com.example.coroutinesbasics.koin

import androidx.work.WorkManager
import com.example.coroutinesbasics.MainViewModel
import com.example.coroutinesbasics.db.getTMBDMovieDatabase
import com.example.coroutinesbasics.network.getTMDBApiService
import com.example.coroutinesbasics.repository.TMBDRepository
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { getTMDBApiService() }
    single { getTMBDMovieDatabase(androidContext()).tmbdMovieDao }
    single { TMBDRepository(get(), get()) }
    single { WorkManager.getInstance(androidApplication()) }
    viewModel { MainViewModel(get(), get()) }
}
