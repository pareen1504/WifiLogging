package com.pd.wifilogging.viewmodel

import android.net.wifi.ScanResult
import androidx.lifecycle.ViewModel
import com.pd.wifilogging.repository.WifiScanRepository
import kotlinx.coroutines.*

class MainActivityViewModel(val wifiScanRepository: WifiScanRepository) : ViewModel() {

    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    val getScanResult = wifiScanRepository.getAllResults()


    fun startMonitoring(listData: List<ScanResult>) {
        uiScope.launch {
            insertData(listData)
        }
    }

    private suspend fun insertData(listData: List<ScanResult>) {
        withContext(Dispatchers.IO) {
            wifiScanRepository.insert(listData)
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