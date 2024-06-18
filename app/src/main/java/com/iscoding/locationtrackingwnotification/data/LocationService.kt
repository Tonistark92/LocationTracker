package com.iscoding.locationtrackingwnotification.data

import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.LocationServices
import com.iscoding.locationtrackingwnotification.R
import com.iscoding.locationtrackingwnotification.data.DefaultLocationClient.Companion.ACTION_LOCATION_ERROR
import com.iscoding.locationtrackingwnotification.data.DefaultLocationClient.Companion.EXTRA_ERROR_MESSAGE
import com.iscoding.locationtrackingwnotification.domain.LocationTracker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class LocationService : Service() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var locationClient: LocationTracker

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        locationClient = DefaultLocationClient(
            applicationContext,
            LocationServices.getFusedLocationProviderClient(applicationContext)
        )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> start()
            ACTION_STOP -> stop()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun start() {
        sendErrorBroadcast("HELLO FROM SERVICE")

        val notification = NotificationCompat.Builder(this, "location")
            .setContentTitle("Tracking location...")
            .setContentText("Location: null")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setOngoing(true)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        Log.d("ISLAM","SERVICE STARTED " )

        locationClient
            .getLocationUpdates(8000L) // Adjust interval as needed
            .catch { e ->
                sendErrorBroadcast(e.message.toString())

                e.printStackTrace()
            }
            .onEach { location ->
                Log.d("ISLAM", location.latitude.toString())
                val lat = location.latitude.toString()
                val long = location.longitude.toString()
                val updatedNotification = notification.setContentText(
                    "Location: ($lat, $long)"
                )
                notificationManager.notify(1, updatedNotification.build())
            }
            .launchIn(serviceScope)

        startForeground(1, notification.build())
    }

    private fun stop() {
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
    }
    private fun sendErrorBroadcast(message: String) {
        val intent = Intent(ACTION_LOCATION_ERROR).apply {
            putExtra(EXTRA_ERROR_MESSAGE, message)
        }
        this.sendBroadcast(intent)
    }
}