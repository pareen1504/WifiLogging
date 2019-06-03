package com.pd.wifilogging.model.database

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.pd.wifilogging.utils.observeOnce
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

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
        val checkdata = databasedao.getAllScanData()
        checkdata.observeOnce { list ->
            assertEquals(list.get(0).wifiname, listData.wifiname)
            assertEquals(list.get(0).wifiStrength, listData.wifiStrength)
        }

    }
}