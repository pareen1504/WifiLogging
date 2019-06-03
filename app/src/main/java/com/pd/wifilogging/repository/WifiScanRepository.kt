package com.pd.wifilogging.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.pd.wifilogging.R
import com.pd.wifilogging.model.database.ListData
import com.pd.wifilogging.model.database.WifiDatabase
import com.pd.wifilogging.model.database.WifiDatabasedao
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException

class WifiScanRepository(application: Application) {

    private var dao: WifiDatabasedao
    lateinit var getAllScanResults: LiveData<List<ListData>>
    var storage_permission: Boolean = false
    val filePath: String

    init {
        val database = WifiDatabase.getInstance(application)
        dao = database.wifiDatabasedao
        filePath = application.resources.getString(R.string.filepath)
    }

    fun getAllResults(): LiveData<List<ListData>> {
        getAllScanResults = dao.getAllScanData()
        return getAllScanResults
    }

    fun insert(listData: ListData) {
        dao.insert(listData)
        if (storage_permission)
            appendLog("Wifi Name:${listData.wifiname} Strength in db : ${listData.wifiStrength}")
    }

    fun delete(ssid: String) {
        dao.delete(ssid)
    }


    private fun appendLog(text: String) {
        val logFile = File(filePath)
        if (!logFile.exists()) {
            try {
                logFile.createNewFile()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        try {
            val buf = BufferedWriter(FileWriter(logFile, true)).apply {
                append(text)
                newLine()
                close()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    fun checkStoragePermission(status: Boolean) {
        this.storage_permission = status
    }
}