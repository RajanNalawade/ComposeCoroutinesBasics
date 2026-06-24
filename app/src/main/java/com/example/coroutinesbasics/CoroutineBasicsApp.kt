package com.example.coroutinesbasics

import android.app.Application
import androidx.work.Configuration
import androidx.work.WorkManager
import com.example.coroutinesbasics.koin.appModule
import com.example.coroutinesbasics.worker.CustomWorkerFactory
import com.example.coroutinesbasics.worker.TMBDMovieWorker
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class CoroutineBasicsApp : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@CoroutineBasicsApp)
            modules(appModule)
        }

        // Tell WorkManager to use your custom WorkerFactory
        val config = Configuration.Builder()
            .setWorkerFactory(CustomWorkerFactory())
            .build()
        WorkManager.initialize(this, config)
    }
}