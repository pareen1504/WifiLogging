package com.pd.wifilogging.model.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface WifiDatabasedao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(listData: ListData)

    @Query("DELETE FROM wifi_log_table WHERE wifiname = :ssid ")
    fun delete(ssid: String)

    @Query("SELECT * FROM wifi_log_table ORDER BY wifiStrength DESC")
    fun getAllScanData(): LiveData<List<ListData>>


}