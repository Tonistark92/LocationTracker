package com.iscoding.locationtrackingwnotification.data


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import com.iscoding.locationtrackingwnotification.data.util.hasLocationPermission
import com.iscoding.locationtrackingwnotification.domain.LocationTracker
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch

class DefaultLocationClient(
    private val context: Context,
    private val client: FusedLocationProviderClient
) : LocationTracker {

    companion object {
        const val ACTION_LOCATION_ERROR = "com.iscoding.locationtrackingwnotification.ACTION_LOCATION_ERROR"
        const val EXTRA_ERROR_MESSAGE = "com.iscoding.locationtrackingwnotification.EXTRA_ERROR_MESSAGE"
    }

    @SuppressLint("MissingPermission")
    override fun getLocationUpdates(interval: Long): Flow<Location> {
        return callbackFlow {
            try {
//                if (!context.hasLocationPermission()) {
//                    throw LocationTracker.LocationException("Missing location permission")
//                }
//
//                val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
//
//                val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
//                if (!isGpsEnabled) {
//                    throw LocationTracker.LocationException("GPS is disabled")
//                }
                sendErrorBroadcast("HELLO FROM LOCATION TRACKER")

                val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, interval)
                    .setWaitForAccurateLocation(false)
                    .setMinUpdateIntervalMillis(5000)
                    .setMaxUpdateDelayMillis(2000)
                    .setMinUpdateDistanceMeters(3f)
                    .build()

                val locationCallback = object : LocationCallback() {
                    override fun onLocationResult(result: LocationResult) {
                        super.onLocationResult(result)
                        result.locations.lastOrNull()?.let { location ->
                            launch { send(location) }
                        }
                    }
                }

                client.requestLocationUpdates(
                    request,
                    locationCallback,
                    Looper.getMainLooper()
                )

                awaitClose {
                    client.removeLocationUpdates(locationCallback)
                }
            } catch (e: Exception) {
                sendErrorBroadcast(e.message.toString())
            }
        }

    }
    private fun sendErrorBroadcast(message: String) {
        val intent = Intent(ACTION_LOCATION_ERROR).apply {
            putExtra(EXTRA_ERROR_MESSAGE, message)
        }
        context.sendBroadcast(intent)
    }
}