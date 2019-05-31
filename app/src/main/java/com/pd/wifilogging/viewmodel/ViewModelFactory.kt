package com.pd.wifilogging.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.pd.wifilogging.model.database.WifiDatabasedao


class ViewModelFactory(
    private val databasedao:WifiDatabasedao,
    private val application: Application): ViewModelProvider.Factory{

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainActivityViewModel::class.java)){
            return MainActivityViewModel(databasedao,application) as T
        }

        throw IllegalArgumentException("Unknown Viewmodel class")
    }

}
