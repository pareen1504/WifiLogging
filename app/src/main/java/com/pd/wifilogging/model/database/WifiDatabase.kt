package com.pd.wifilogging.model.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = arrayOf(ListData::class), version = 1, exportSchema = false)
abstract class WifiDatabase : RoomDatabase() {
    abstract val wifiDatabasedao: WifiDatabasedao

    companion object {

        @Volatile
        private var INSTANCE: WifiDatabase? = null

        fun getInstance(context: Context): WifiDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        WifiDatabase::class.java, "wifi_scanlist_database"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE == instance
                }
                return instance
            }

        }
    }
}