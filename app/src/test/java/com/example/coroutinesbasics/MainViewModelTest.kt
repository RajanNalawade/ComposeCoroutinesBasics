package com.example.coroutinesbasics


import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.coroutinesbasics.utils.MainCoroutineScopeRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Rule

class MainViewModelTest {
    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val coroutineScope = MainCoroutineScopeRule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    lateinit var subject: MainViewModel

    /*@Before
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
    }*/

}