package com.pd.wifilogging.viewmodel

import androidx.lifecycle.ViewModel
import com.pd.wifilogging.model.database.ListData
import com.pd.wifilogging.repository.WifiScanRepository
import kotlinx.coroutines.*

class MainActivityViewModel(val wifiScanRepository: WifiScanRepository) : ViewModel() {

    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    val getScanResult = wifiScanRepository.getAllResults()


    fun startMonitoring(listData: ListData) {
        uiScope.launch {
            insertData(listData)
        }
    }

    suspend fun insertData(listData: ListData) {
        withContext(Dispatchers.IO) {
            wifiScanRepository.insert(listData)
        }
    }

    fun removeTestData(ssid: String) {
        uiScope.launch {
            delete(ssid)
        }
    }

    suspend fun delete(ssid: String) {
        withContext(Dispatchers.IO) {
            wifiScanRepository.delete(ssid)
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    fun isGranted(ispermissionGranted: Boolean) {
        wifiScanRepository.checkStoragePermission(ispermissionGranted)
    }

}