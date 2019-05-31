package com.pd.wifilogging.model.database

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class WifiDatabaseTest {
    private lateinit var databasedao: WifiDatabasedao
    private lateinit var database: WifiDatabase

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun SetUp() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        database = Room.inMemoryDatabaseBuilder(context, WifiDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        databasedao = database.wifiDatabasedao
    }

    @After
    @Throws(IOException::class)
    fun TearDown() {
        database.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetWifilist() {
        val listData = ListData("SKYABD578_WIFI", -73)
        databasedao.insert(listData)
        val checkdata = getValue(databasedao.getAllScanData())
        assertEquals(checkdata.get(0).wifiname,listData.wifiname)

    }


    @Suppress("UNCHECKED_CAST")
    @Throws(InterruptedException::class)
    fun <T> getValue(liveData: LiveData<T>): T {
        val data = arrayOfNulls<Any>(1)
        val latch = CountDownLatch(1)
        val observer = object : Observer<T> {
            override fun onChanged(t: T?) {
                data[0] = t
                latch.countDown()
                liveData.removeObserver(this)
            }

        }
        liveData.observeForever(observer)
        latch.await(2, TimeUnit.SECONDS)

        return data[0] as T
    }
}