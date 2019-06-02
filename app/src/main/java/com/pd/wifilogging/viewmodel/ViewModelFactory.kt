package com.pd.wifilogging.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.pd.wifilogging.model.database.WifiDatabasedao
import com.pd.wifilogging.repository.WifiScanRepository


class ViewModelFactory(
    private val repository: WifiScanRepository): ViewModelProvider.Factory{

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainActivityViewModel::class.java)){
            return MainActivityViewModel(repository) as T
        }

        throw IllegalArgumentException("Unknown Viewmodel class")
    }

}
