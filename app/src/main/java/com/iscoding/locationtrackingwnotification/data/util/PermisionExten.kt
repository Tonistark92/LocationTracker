package com.iscoding.locationtrackingwnotification.data.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

fun Context.hasLocationPermission(): Boolean {
    return ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
//            &&  ContextCompat.checkSelfPermission(
//                this,
//                Manifest.permission.FOREGROUND_SERVICE
//            ) == PackageManager.PERMISSION_GRANTED &&
//            ContextCompat.checkSelfPermission(
//                this,
//                Manifest.permission.FOREGROUND_SERVICE_LOCATION
//            ) == PackageManager.PERMISSION_GRANTED &&
//            ContextCompat.checkSelfPermission(
//                this,
//                Manifest.permission.POST_NOTIFICATIONS
//            ) == PackageManager.PERMISSION_GRANTED
}