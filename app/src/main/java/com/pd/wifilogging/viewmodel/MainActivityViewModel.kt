package com.pd.wifilogging.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.pd.wifilogging.model.database.ListData
import com.pd.wifilogging.model.database.WifiDatabasedao
import kotlinx.coroutines.*

class MainActivityViewModel(
    val wifiDatabasedao: WifiDatabasedao,
    application: Application
) : AndroidViewModel(application) {

    val getScanResult = wifiDatabasedao.getAllScanData()
    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    fun startMonitoring(listData: ListData) {
        uiScope.launch {
            insertData(listData)
        }
    }

    private suspend fun insertData(listData: ListData) {
        withContext(Dispatchers.IO) {
            wifiDatabasedao.insert(listData)
        }
    }


    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

}