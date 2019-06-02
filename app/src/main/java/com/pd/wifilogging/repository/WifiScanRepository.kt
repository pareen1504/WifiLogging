package com.pd.wifilogging.repository

import android.app.Application
import android.net.wifi.ScanResult
import android.util.Log
import androidx.lifecycle.LiveData
import com.pd.wifilogging.model.database.ListData
import com.pd.wifilogging.model.database.WifiDatabase
import com.pd.wifilogging.model.database.WifiDatabasedao
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException

class WifiScanRepository(application: Application) {

    private var dao: WifiDatabasedao
    private var getAllScanResults: LiveData<List<ListData>>
    private var storage_permission : Boolean = false

    init {
        val database = WifiDatabase.getInstance(application)
        dao = database.wifiDatabasedao
        getAllScanResults = dao.getAllScanData()
    }

    fun getAllResults(): LiveData<List<ListData>> {
        return getAllScanResults
    }

    fun insert(listData: List<ScanResult>) {
        for (item in listData) {
            Log.e("WifiScanRepository", "${item.SSID} ${item.level}")
            if (item.SSID.isNotEmpty()) {
                val data = ListData(item.SSID, item.level)
                dao.insert(data)
                if (storage_permission)
                appendLog("Wifi Name:${data.wifiname} Strength in db : ${data.wifiStrength}"  )
            }
        }
    }

    private fun appendLog(text: String) {
        val logFile = File("sdcard/log.file")
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

    fun checkStoragePermission(status:Boolean){
        this.storage_permission = status
    }
}