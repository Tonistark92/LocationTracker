package com.iscoding.locationtrackingwnotification.data.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
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
            &&  ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.FOREGROUND_SERVICE
            ) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.FOREGROUND_SERVICE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
}

//fun Context.hasLocationPermission(): Boolean {
//    val basePermissionsGranted = ContextCompat.checkSelfPermission(
//        this,
//        Manifest.permission.ACCESS_COARSE_LOCATION
//    ) == PackageManager.PERMISSION_GRANTED &&
//            ContextCompat.checkSelfPermission(
//                this,
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) == PackageManager.PERMISSION_GRANTED
//
//    val apiSpecificPermissionsGranted = when {
//        Build.VERSION.SDK_INT >= 34 -> {
//            ContextCompat.checkSelfPermission(
//                this,
//                Manifest.permission.FOREGROUND_SERVICE_LOCATION
//            ) == PackageManager.PERMISSION_GRANTED &&
//                    ContextCompat.checkSelfPermission(
//                        this,
//                        Manifest.permission.POST_NOTIFICATIONS
//                    ) == PackageManager.PERMISSION_GRANTED
//        }
//        Build.VERSION.SDK_INT >= 33 -> {
//            ContextCompat.checkSelfPermission(
//                this,
//                Manifest.permission.POST_NOTIFICATIONS
//            ) == PackageManager.PERMISSION_GRANTED
//        }
//        Build.VERSION.SDK_INT >= 28 -> {
//            ContextCompat.checkSelfPermission(
//                this,
//                Manifest.permission.FOREGROUND_SERVICE
//            ) == PackageManager.PERMISSION_GRANTED
//        }
//        else -> true // No additional permissions required for lower API levels
//    }
//
//    return basePermissionsGranted && apiSpecificPermissionsGranted
//}