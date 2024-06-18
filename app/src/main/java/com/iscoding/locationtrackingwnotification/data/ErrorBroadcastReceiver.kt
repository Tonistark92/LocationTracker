package com.iscoding.locationtrackingwnotification.data

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class ErrorBroadcastReceiver(private val onErrorReceived: (String) -> Unit) : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val errorMessage = intent?.getStringExtra(DefaultLocationClient.EXTRA_ERROR_MESSAGE)
        Log.d("ISLAM",errorMessage +"HEEEEEELP" )

        errorMessage?.let { onErrorReceived(it) }
    }
}