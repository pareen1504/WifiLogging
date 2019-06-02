package com.pd.wifilogging

import android.Manifest
import android.content.*
import android.content.pm.PackageManager
import android.net.wifi.WifiManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import com.pd.wifilogging.adapter.WifiListAdapter
import com.pd.wifilogging.databinding.ActivityMainBinding
import com.pd.wifilogging.repository.WifiScanRepository
import com.pd.wifilogging.utils.*
import com.pd.wifilogging.viewmodel.MainActivityViewModel
import com.pd.wifilogging.viewmodel.ViewModelFactory

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainActivityViewModel
    private lateinit var binding: ActivityMainBinding
    private lateinit var wifiManager: WifiManager

    private val filter = IntentFilter().apply {
        addAction(WifiManager.RSSI_CHANGED_ACTION)
    }

    private val wifiScanReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                WifiManager.RSSI_CHANGED_ACTION -> {
                    viewModel.startMonitoring(wifiManager.scanResults)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

        val application = requireNotNull(this).application
        val repository = WifiScanRepository(application)
        val viewModelFactory = ViewModelFactory(repository)

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

        if (versionAndroidPieandAbove()) locationpermission()
        storagepermission()

    }

    private fun locationpermission() {
        if (!isPermissionGranted(Manifest.permission.ACCESS_COARSE_LOCATION) &&
            !isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION)
        ) {
            requestlocationpermission()
        } else {
            createLocationRequest()
        }
    }

    private fun requestlocationpermission() {
        if (shouldShowPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION) &&
            shouldShowPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)
        ) {
            batchRequestPermissions(PERMISSIONS_LOCATION, REQUEST_PERMISSIONS_LOCATION)
        } else {
            batchRequestPermissions(PERMISSIONS_LOCATION, REQUEST_PERMISSIONS_LOCATION)
        }
    }

    private fun storagepermission() {
        if (!isPermissionGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        ) {
            viewModel.isGranted(false)
            requestStoragepermission()
        } else {
            viewModel.isGranted(true)
        }
    }

    private fun requestStoragepermission() {
        if (shouldShowPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        ) {
            requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, REQUEST_PERMISSIONS_STORAGE)
        } else {
            requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, REQUEST_PERMISSIONS_STORAGE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_PERMISSIONS_LOCATION -> {
                if (grantResults.containsOnly(PackageManager.PERMISSION_GRANTED)) {
                    createLocationRequest()
                }
            }
            REQUEST_PERMISSIONS_STORAGE -> viewModel.isGranted(true)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CHECK_SETTINGS -> startWifiService(wifiManager)
        }
    }

    @Suppress("DEPRECATION")
    private fun startWifiService(wifiManager: WifiManager) {
        if (!wifiManager.isWifiEnabled) {
            wifiManager.setWifiEnabled(true)
        }
        wifiManager.startScan()
        registerReceiver(wifiScanReceiver, filter)
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

        task.addOnSuccessListener { locationSettingsResponse -> startWifiService(wifiManager) }

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

    override fun onResume() {
        super.onResume()
        if (versionAndroidPieandAbove()) locationpermission()
        storagepermission()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(wifiScanReceiver)
    }

    companion object {

        val PERMISSIONS_LOCATION = arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

}


