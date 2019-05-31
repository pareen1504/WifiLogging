package com.pd.wifilogging

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.os.*
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.PRIORITY_MIN
import com.pd.wifilogging.model.database.ListData
import com.pd.wifilogging.model.database.WifiDatabase
import com.pd.wifilogging.model.database.WifiDatabasedao

class WifiLoggingService : Service() {
    lateinit var listData: ListData
    private val wifiDatabasedao: WifiDatabasedao? = null
    lateinit var wifiManager: WifiManager
    lateinit var wifiScanReceiver: BroadcastReceiver
    private var serviceHandler: ServiceHandler? = null
    private var serviceLooper: Looper? = null
    internal lateinit  var application :Application
    private lateinit var datasource: WifiDatabasedao

    private inner class ServiceHandler(looper: Looper) : Handler(looper) {

        override fun handleMessage(msg: Message) {

            Log.i("WifiLoggingService", "ServiceHandler")
            datasource = WifiDatabase.getInstance(application).wifiDatabasedao
            wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            wifiManager.startScan()

            wifiScanReceiver = object : BroadcastReceiver() {

                override fun onReceive(context: Context, intent: Intent) {
                    val results = wifiManager.scanResults
                    for (item in results) {
                        Log.e("scanResult","${item.SSID}  ${item.level} ")
                        listData = ListData(item.SSID,item.level)
                        datasource.insert(listData)
                    }
                }
            }

            val intentFilter = IntentFilter()
            intentFilter.addAction(WifiManager.RSSI_CHANGED_ACTION)
            registerReceiver(wifiScanReceiver, intentFilter)
        }
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        serviceHandler?.obtainMessage()?.also { msg ->
            msg.arg1 = startId
            serviceHandler?.sendMessage(msg)
        }
        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        application = requireNotNull(this).applicationContext as Application
        startForeground()
        HandlerThread("ServiceStartArguments", Process.THREAD_PRIORITY_BACKGROUND).apply {
            start()
            serviceLooper = looper
            serviceHandler = ServiceHandler(looper)
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun startForeground() {
        val channelId =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createNotificationChannel()
            } else {
                ""
            }

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
        val notification = notificationBuilder.setOngoing(true)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setPriority(PRIORITY_MIN)
            .setCategory(Notification.CATEGORY_SERVICE)
            .build()

        startForeground(101, notification)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(): String {
        val channelId = "my_service"
        val channelName = "My Background Service"
        val chan = NotificationChannel(
            channelId,
            channelName, NotificationManager.IMPORTANCE_HIGH
        )
        chan.lightColor = Color.BLUE
        chan.importance = NotificationManager.IMPORTANCE_NONE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(chan)
        return channelId
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i("WifiLoggingService", "Destroy")
        unregisterReceiver(wifiScanReceiver)
    }
}