package com.example.coroutinesbasics.worker

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.example.coroutinesbasics.network.getTMDBApiService

class CustomWorkerFactory() : WorkerFactory() {
    override fun createWorker(
        appContext: Context, workerClassName: String, workerParameters: WorkerParameters
    ): ListenableWorker? {
        return when (workerClassName) {

            TMBDMovieWorker::class.java.name -> {
                TMBDMovieWorker(appContext, workerParameters, getTMDBApiService())
            }

            else -> {
                // Fallback to default WorkManager factory for other workers
                this.createWorker(appContext, workerClassName, workerParameters)
            }
        }
    }
}