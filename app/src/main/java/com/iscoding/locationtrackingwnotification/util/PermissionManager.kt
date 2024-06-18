package com.iscoding.locationtrackingwnotification.util

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.iscoding.locationtrackingwnotification.MainActivity

class PermissionManager(
    caller: ActivityResultCaller,
    private val context: Context,
    private val fragmentManager: FragmentManager?,
    private val shouldShowPermissionRationale: (permission: String) -> Boolean,
     val permissionExplanationCallback: PermissionExplanationCallback? = null
) {
    private var onPermissionsGranted: ((isGranted: Boolean) -> Unit)? = null
    private var currentPermission: String? = null // Property for currentPermission
    private var deniedPermissions: List<String> = emptyList() // Property for deniedPermissions

    constructor(activity: ComponentActivity, callback: PermissionExplanationCallback? = null) : this(
        caller = activity,
        context = activity,
        fragmentManager = (activity as AppCompatActivity).supportFragmentManager,
        shouldShowPermissionRationale = { activity.shouldShowRequestPermissionRationale(it) },
        permissionExplanationCallback = callback
    )

    constructor(fragment: Fragment, callback: PermissionExplanationCallback? = null) : this(
        caller = fragment,
        context = fragment.requireContext(),
        fragmentManager = fragment.parentFragmentManager,
        shouldShowPermissionRationale = { fragment.shouldShowRequestPermissionRationale(it) },
        permissionExplanationCallback = callback
    )

    constructor(activity: AppCompatActivity, callback: PermissionExplanationCallback? = null) : this(
        caller = activity,
        context = activity,
        fragmentManager = activity.supportFragmentManager,
        shouldShowPermissionRationale = { activity.shouldShowRequestPermissionRationale(it) },
        permissionExplanationCallback = callback
    )
    constructor(activity: MainActivity, callback: PermissionExplanationCallback? = null) : this(
        caller = activity,
        context = activity,
        fragmentManager =null,
        shouldShowPermissionRationale = { activity.shouldShowRequestPermissionRationale(it) },
        permissionExplanationCallback = callback
    )
    private val requestPermissionLauncher =
        caller.registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (!isGranted) {
                currentPermission?.let { permissionExplanationCallback?.onPermissionDenied(it) }
                requestPermissions(listOf(currentPermission!!))
            }
            onPermissionsGranted?.invoke(isGranted)
        }

    private val requestMultiplePermissionsLauncher =
        caller.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
            deniedPermissions = result.filterValues { !it }.keys.toList()
            val isGranted = deniedPermissions.isEmpty()
            for (l in result){
                if (result.any{it.value == false}) {
                    permissionExplanationCallback?.onPermissionsDenied(deniedPermissions)
                    requestPermissions(deniedPermissions)
                }
            }

            onPermissionsGranted?.invoke(isGranted)
        }

    fun checkPermissions(
        vararg permissions: String,
        onPermissionsGranted: ((isGranted: Boolean) -> Unit)? = null
    ) {
        this.onPermissionsGranted = onPermissionsGranted

        val permissionsToBeRequested = permissions.filter { permission ->
            ContextCompat.checkSelfPermission(
                context,
                permission
            ) != PackageManager.PERMISSION_GRANTED
        }
        val shouldShowRequestPermissionRationale = permissionsToBeRequested.any {
            //check from the activity if should show Rationale
            Log.d("PERMISSIONS", "SHOW RATIONAL *********")
            shouldShowPermissionRationale.invoke(it)
        }

        when {
            permissionsToBeRequested.isEmpty() -> onPermissionsGranted?.invoke(true)
            shouldShowRequestPermissionRationale -> {
                // In an educational UI, explain to the user why your app requires this
                // permission for a specific feature to behave as expected, and what
                // features are disabled if it's declined. In this UI, include a
                // "cancel" or "no thanks" button that lets the user continue
                // using your app without granting the permission.
                Log.d("PERMISSIONS", " permissionExplanationCallback *********")

                permissionExplanationCallback?.onPermissionsDenied(permissionsToBeRequested)
                onPermissionsGranted?.invoke(false)
                requestPermissions(permissionsToBeRequested)
            }
            else -> requestPermissions(permissionsToBeRequested)
        }
    }

    fun checkMediaPermissions(
        vararg permissions: MediaPermission,
        onPermissionsGranted: ((isGranted: Boolean) -> Unit)? = null
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            checkPermissions(
                *permissions.map { it.getGranularMediaPermission() }.toTypedArray(),
                onPermissionsGranted = onPermissionsGranted
            )
        } else {
            checkPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                onPermissionsGranted = onPermissionsGranted
            )
        }
    }
    fun checkLocationPermissions(
        onPermissionsGranted: ((isGranted: Boolean) -> Unit)? = null
    ) {
        val permissions = mutableListOf(
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P ) {
            permissions.addAll(listOf(
                android.Manifest.permission.FOREGROUND_SERVICE,
            ))
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ) {
            permissions.addAll(listOf(
                android.Manifest.permission.POST_NOTIFICATIONS,
            ))
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE ) {
            permissions.addAll(listOf(
                android.Manifest.permission.FOREGROUND_SERVICE_LOCATION,
            ))
        }

        checkPermissions(
            *permissions.toTypedArray(),
            onPermissionsGranted = onPermissionsGranted
        )
    }

    private fun requestPermissions(permissionsToBeRequested: List<String>) {
        deniedPermissions = emptyList() // Reset deniedPermissions before each request
        if (permissionsToBeRequested.size > 1) {
            currentPermission = null // Reset currentPermission when requesting multiple permissions
            requestMultiplePermissionsLauncher.launch(permissionsToBeRequested.toTypedArray())
        } else {
            permissionsToBeRequested.firstOrNull()?.let { permission ->
                currentPermission = permission // Set currentPermission for single permission
                requestPermissionLauncher.launch(permission)
            }
        }
    }

    enum class MediaPermission {
        IMAGES {
            @RequiresApi(Build.VERSION_CODES.TIRAMISU)
            override fun getGranularMediaPermission() = Manifest.permission.READ_MEDIA_IMAGES
        },
        VIDEO {
            @RequiresApi(Build.VERSION_CODES.TIRAMISU)
            override fun getGranularMediaPermission() = Manifest.permission.READ_MEDIA_VIDEO
        },
        AUDIO {
            @RequiresApi(Build.VERSION_CODES.TIRAMISU)
            override fun getGranularMediaPermission() = Manifest.permission.READ_MEDIA_AUDIO
        };

        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        abstract fun getGranularMediaPermission(): String
    }
}
interface PermissionExplanationCallback {
    fun onPermissionDenied(permission: String)
    fun onPermissionsDenied(permissions: List<String>)
}