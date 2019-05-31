package com.pd.wifilogging

import android.Manifest
import android.content.*
import android.content.pm.PackageManager
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import com.pd.wifilogging.viewmodel.MainActivityViewModel
import com.pd.wifilogging.databinding.ActivityMainBinding
import com.pd.wifilogging.adapter.WifiListAdapter
import com.pd.wifilogging.model.database.ListData
import com.pd.wifilogging.model.database.WifiDatabase
import com.pd.wifilogging.utils.REQUEST_CHECK_SETTINGS
import com.pd.wifilogging.utils.REQUEST_PERMISSION_LOCATION
import com.pd.wifilogging.viewmodel.ViewModelFactory

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainActivityViewModel
    private lateinit var binding: ActivityMainBinding
    lateinit var listData: ListData
    lateinit var wifiManager: WifiManager
    private lateinit var wifiScanReceiver: BroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        val application = requireNotNull(this).application
        val datasource = WifiDatabase.getInstance(application).wifiDatabasedao
        val viewModelFactory = ViewModelFactory(datasource, application)

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(MainActivityViewModel::class.java)
        binding.viewmodel = viewModel

        val adapter = WifiListAdapter()
        binding.wifiRecylerview.adapter = adapter

        viewModel.getScanResult.observe(this, Observer { list ->
            list?.let {
                adapter.submitList(it)
            }
        })

        binding.lifecycleOwner = this
        askPermissions()
    }

    private fun askPermissions() {
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            ) {
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ),
                    REQUEST_PERMISSION_LOCATION
                )
            }
        } else {
            if (!versionAndroidPieandAbove()) {
                startWifiService()
            } else {
                createLocationRequest()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_PERMISSION_LOCATION -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED))
                    if (!versionAndroidPieandAbove()) {
                        startWifiService()
                    } else {
                        createLocationRequest()
                    }
                else {
                }
                return
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CHECK_SETTINGS -> startWifiService()
        }
    }

    private fun startWifiService() {

        wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        wifiManager.setWifiEnabled(true)
        wifiManager.startScan()

        wifiScanReceiver = object : BroadcastReceiver() {

            override fun onReceive(context: Context, intent: Intent) {
                val results = wifiManager.scanResults
                for (item in results) {
                    if (item.SSID.isNotEmpty())
                        listData = ListData(item.SSID, item.level)
                    viewModel.startMonitoring(listData)
                }
            }
        }

        val intentFilter = IntentFilter()
        intentFilter.addAction(WifiManager.RSSI_CHANGED_ACTION)
        registerReceiver(wifiScanReceiver, intentFilter)

    }

    private fun createLocationRequest() {
        val locationRequest = LocationRequest.create()?.apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        }

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest!!)

        val client: SettingsClient = LocationServices.getSettingsClient(this)
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener { locationSettingsResponse ->
            startWifiService()
        }

        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                try {
                    exception.startResolutionForResult(
                        this@MainActivity,
                        REQUEST_CHECK_SETTINGS
                    )
                } catch (sendEx: IntentSender.SendIntentException) {
                }
            }
        }
    }

    private fun versionAndroidPieandAbove(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.P


    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(wifiScanReceiver)
    }
}
