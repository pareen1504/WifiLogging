package com.pd.wifilogging.utils

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.LiveData

fun AppCompatActivity.isPermissionGranted(permission: String) =
    ActivityCompat.checkSelfPermission(this, permission) == android.content.pm.PackageManager.PERMISSION_GRANTED

fun AppCompatActivity.shouldShowPermissionRationale(permission: String) =
    ActivityCompat.shouldShowRequestPermissionRationale(this, permission)

fun AppCompatActivity.requestPermission(permission: String, requestId: Int) =
    ActivityCompat.requestPermissions(this, arrayOf(permission), requestId)

fun AppCompatActivity.batchRequestPermissions(permissions: Array<String>, requestId: Int) =
    ActivityCompat.requestPermissions(this, permissions, requestId)

fun versionAndroidPieandAbove(): Boolean {
    return Build.VERSION.SDK_INT > Build.VERSION_CODES.O
}

fun IntArray.containsOnly(num: Int): Boolean = filter { it == num }.isNotEmpty()

fun <T> LiveData<T>.observeOnce(onChangeHandler: (T) -> Unit) {
    val observer = OneTimeObserver(handler = onChangeHandler)
    observe(observer, observer)
}