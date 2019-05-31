package com.pd.wifilogging.model.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface WifiDatabasedao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(listData: ListData)

    @Update
    fun update(listData: ListData)

    @Query("SELECT * FROM wifi_log_table ORDER BY wifiStrength DESC")
    fun getAllScanData(): LiveData<List<ListData>>


}