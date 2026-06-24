package com.example.coroutinesbasics.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.coroutinesbasics.db.TmdbMovieDatabase
import com.example.coroutinesbasics.db.getTMBDMovieDatabase
import com.example.coroutinesbasics.network.TmdbApi
import com.example.coroutinesbasics.repository.TMBDRepository

class TMBDMovieWorker(
    context: Context, params: WorkerParameters, private val tmdbApi: TmdbApi
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val tmdbMovieDatabase = getTMBDMovieDatabase(applicationContext).tmbdMovieDao
        val tmbdRepository = TMBDRepository(tmdbApi, tmdbMovieDatabase)

        return try {
            tmbdRepository.getPopularMoviesFirstSave()
            Result.Success()
        }catch (error: Throwable){
            Result.Failure()
        }
    }
}