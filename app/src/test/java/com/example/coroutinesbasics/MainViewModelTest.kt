package com.example.coroutinesbasics


import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.work.WorkManager
import androidx.work.testing.WorkManagerTestInitHelper
import com.example.coroutinesbasics.db.Title
import com.example.coroutinesbasics.db.TitleDao
import com.example.coroutinesbasics.network.MainNetwork
import com.example.coroutinesbasics.repository.TitleRepository
import com.example.coroutinesbasics.utils.MainCoroutineScopeRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MainViewModelTest {
    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val coroutineScope = MainCoroutineScopeRule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    lateinit var subject: MainViewModel

    @Before
    fun setUp() {
        val repository = TitleRepository(
            network = object : MainNetwork {
                override suspend fun fetchNextTitle(): Title = throw NotImplementedError()
            },
            titleDao = object : TitleDao{
                override suspend fun insertTitle(title: Title) {}
                override val titleFlowData: Flow<Title?>
                    get() = MutableStateFlow(Title(title = "test title", 0))
            }
        )
        val context = ApplicationProvider.getApplicationContext<Context>()
        // 1. Initialize WorkManager in a special test-isolated mode
        WorkManagerTestInitHelper.initializeTestWorkManager(context)
        val workManager = WorkManager.getInstance(context)
        subject = MainViewModel(repository, workManager = workManager)
    }

    @Test
    fun checkInitValues(){
        Assert.assertEquals(0, subject.noOfTaps.value)
    }

    @Test
    fun whenUserTapsOnOkClicked(){
        subject.onOkClicked()
        coroutineScope.dispatcher.scheduler.advanceUntilIdle()
        Assert.assertEquals(1, subject.noOfTaps.value)
    }

    @Test
    fun whenUserTapsOnResetClicked(){
        subject.onOkClicked()
        coroutineScope.dispatcher.scheduler.advanceUntilIdle()
        Assert.assertEquals(1, subject.noOfTaps.value)
        subject.onResetClicked()
        coroutineScope.dispatcher.scheduler.advanceUntilIdle()
        Assert.assertEquals(0, subject.noOfTaps.value)
    }

}