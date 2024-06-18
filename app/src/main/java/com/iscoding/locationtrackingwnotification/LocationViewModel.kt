package com.iscoding.locationtrackingwnotification

import android.app.Application
import android.content.Context
import android.content.IntentFilter
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.iscoding.locationtrackingwnotification.data.DefaultLocationClient
import com.iscoding.locationtrackingwnotification.data.ErrorBroadcastReceiver

class LocationViewModel : ViewModel() {
    private val errorReceiver = ErrorBroadcastReceiver { errorMessage ->
        handleErrorMessage(errorMessage)
    }

    init {
        // Register the ErrorBroadcastReceiver
//        applicationContext.registerReceiver(
//            errorReceiver,
//            IntentFilter(DefaultLocationClient.ACTION_LOCATION_ERROR)
//        )
    }
    private fun handleErrorMessage(errorMessage: String) {
        // Handle error message here (e.g., show error to the user)
        Log.d("ISLAM", "$errorMessage HEEEEEEELP")
    }
fun regesterReciver(context: Context){
    context.registerReceiver(
        errorReceiver,
        IntentFilter(DefaultLocationClient.ACTION_LOCATION_ERROR)
    )
}
    fun unRegesterReciver(context: Context){
        context.unregisterReceiver(errorReceiver)
    }
//    fun startLocationService() {
//        val startIntent = Intent(this, LocationService::class.java)
//        startIntent.action = LocationService.ACTION_START
//        applicationContext.startService(startIntent)
//    }
//
//    fun stopLocationService() {
//        val stopIntent = Intent(applicationContext, LocationService::class.java)
//        stopIntent.action = LocationService.ACTION_STOP
//        applicationContext.startService(stopIntent)
//    }

}