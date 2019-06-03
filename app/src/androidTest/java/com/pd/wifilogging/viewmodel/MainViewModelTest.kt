package com.pd.wifilogging.viewmodel

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.pd.wifilogging.model.database.ListData
import com.pd.wifilogging.repository.WifiScanRepository
import com.pd.wifilogging.utils.observeOnce
import kotlinx.coroutines.runBlocking
import org.junit.*
import org.junit.runner.RunWith
import java.io.IOException


@RunWith(AndroidJUnit4::class)
class MainViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    lateinit var mainViewModel: MainActivityViewModel
    lateinit var result: ListData
    lateinit var repository: WifiScanRepository


    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext.applicationContext
        val application = requireNotNull(context) as Application
        repository = WifiScanRepository(application)
        this.mainViewModel = MainActivityViewModel(repository)
        buildScanResult("abcdxyz", -50)

    }

    @After
    @Throws(IOException::class)
    fun tearDown() {
        runBlocking {
            mainViewModel.delete(result.wifiname)
        }
    }

    private fun buildScanResult(ssid: String, level: Int) {
        result = ListData(ssid, level)
    }

    @Test
    @Throws(Exception::class)
    fun startMonitoring() {
        runBlocking {
            mainViewModel.insertData(result)
        }

        this.mainViewModel.getScanResult.observeOnce {
            Assert.assertEquals(it.last().wifiname, result.wifiname)
        }
    }
}
