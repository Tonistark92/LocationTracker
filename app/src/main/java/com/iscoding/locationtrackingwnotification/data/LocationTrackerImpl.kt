package com.iscoding.locationtrackingwnotification.data


import android.annotation.SuppressLint
import android.content.Context
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
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch

class DefaultLocationClient(
    private val context: Context,
    private val client: FusedLocationProviderClient
): LocationTracker {

    @SuppressLint("MissingPermission")
    override fun getLocationUpdates(interval: Long): Flow<Location> {
        return callbackFlow {
            if(!context.hasLocationPermission()) {
                throw LocationTracker.LocationException("Missing location permission")
            }

//            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
//            val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
//            val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
//            if(!isGpsEnabled && !isNetworkEnabled) {
//                throw LocationTracker.LocationException("GPS is disabled")
//            }

            val request = LocationRequest.create()
                .setInterval(interval)
                .setFastestInterval(5000)
                .setSmallestDisplacement(4f)
                .setPriority( Priority. PRIORITY_HIGH_ACCURACY)

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
        }
    }
}