package com.iscoding.locationtrackingwnotification

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.iscoding.locationtrackingwnotification.data.LocationService
import com.iscoding.locationtrackingwnotification.ui.theme.LocationTrackingWNotificationTheme
import com.iscoding.locationtrackingwnotification.util.PermissionExplanationCallback
import com.iscoding.locationtrackingwnotification.util.PermissionManager
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity(), PermissionExplanationCallback {
    private lateinit var permissionManager: PermissionManager
    val viewmodel :LocationViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewmodel.regesterReciver(this)
        permissionManager = PermissionManager(this as MainActivity)
        setContent {
            LocationTrackingWNotificationTheme {
                val permissionsGranted = remember { mutableStateOf(false) }

                val scope = rememberCoroutineScope()
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Button(onClick = {
                        if (permissionsGranted.value) {
                            startLocationService()
                        } else {
                            scope.launch {
                                requestLocationAndForegroundPermissions(permissionsGranted)
                            }
                        }
                    }) {
                        Text(text = "Start")
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = {
                        stopLocationService()
                    }) {
                        Text(text = "Stop")
                    }
                }
            }
        }
    }
    override fun onPermissionDenied(permission: String) {
        // Show explanation to the user
        Log.d("PERMISSIONS", " permissionExplanationCallback Activity $$$$$$$$$")

        Toast.makeText(this, "Permission $permission was denied.", Toast.LENGTH_SHORT).show()

    }

    override fun onPermissionsDenied(permissions: List<String>) {
        // Show explanation to the user
        Log.d("PERMISSIONS", " permissionExplanationCallback $$$$$$$$")

        for (permission in permissions) {
            Toast.makeText(this, "Permission $permission was denied.", Toast.LENGTH_SHORT).show()
        }
    }
    private fun requestLocationAndForegroundPermissions(permissionsGranted: MutableState<Boolean>) {
//        val permissions = arrayOf(
//            android.Manifest.permission.ACCESS_COARSE_LOCATION,
//            android.Manifest.permission.ACCESS_FINE_LOCATION,
//            android.Manifest.permission.FOREGROUND_SERVICE,
//            android.Manifest.permission.POST_NOTIFICATIONS,
//            android.Manifest.permission.FOREGROUND_SERVICE_LOCATION,
//        )
//
//        permissionManager.checkPermissions(*permissions) { isGranted ->
//            if (isGranted) {
//                // All permissions granted, update the state
//                permissionsGranted.value = true
//                startLocationService()
//            } else {
//                // Handle scenario where permissions were not granted
//                permissionsGranted.value = false
//
//            }
//        }
        permissionManager.checkLocationPermissions { isGranted ->
            if (isGranted) {
                // All permissions granted, update the state
                permissionsGranted.value = true
                startLocationService()
            } else {
                // Handle scenario where permissions were not granted
                permissionsGranted.value = false
                Toast.makeText(this, "Permission  was denied please accept it .", Toast.LENGTH_SHORT).show()

            }
        }
    }

    private fun startLocationService() {
        Intent(applicationContext, LocationService::class.java).apply {
            action = LocationService.ACTION_START
            startService(this)
        }
    }

    private fun stopLocationService() {
        Intent(applicationContext, LocationService::class.java).apply {
            action = LocationService.ACTION_STOP
            startService(this)
        }
    }

}
