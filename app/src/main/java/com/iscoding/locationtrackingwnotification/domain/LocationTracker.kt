package com.iscoding.locationtrackingwnotification.domain

import android.location.Location
import kotlinx.coroutines.flow.Flow

interface LocationTracker {
    fun getLocationUpdates(interval: Long): Flow<Location>

    class LocationException(message: String): Exception()
}