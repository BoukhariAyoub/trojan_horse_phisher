package com.lookmyup.trojanhorse

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.widget.Toast

/**
 * Helper class to start the monitoring service and handle permissions
 */
object ServiceStarter {
    private const val TAG = "TrojanHorse"

    // Flag to track permission request process
    private var isRequestingPermissions = false

    /**
     * Start the permission request flow and monitor service
     * This is the main entry point that should be called from the activity
     */
    fun requestPermissionsAndStartService(activity: Activity) {
        Log.d(TAG, "Starting permission request flow")
        isRequestingPermissions = true

        // First check for overlay permission
        if (!hasOverlayPermission(activity)) {
            Log.d(TAG, "Requesting overlay permission first")
            requestOverlayPermission(activity)
            return
        }

        // Then check for usage stats permission
        if (!hasUsageStatsPermission(activity)) {
            Log.d(TAG, "Requesting usage stats permission")
            requestUsageStatsPermission(activity)
            return
        }

        // If we have all permissions, start the service
        Log.d(TAG, "All permissions granted, starting service")
        startMonitoringService(activity)
        isRequestingPermissions = false
    }

    /**
     * Call this from onResume() to continue the permission flow
     * Returns true if the permission flow is complete
     */
    fun continuePermissionRequestIfNeeded(activity: Activity): Boolean {
        // If we're not in the middle of requesting permissions, do nothing
        if (!isRequestingPermissions) {
            return true
        }

        Log.d(TAG, "Continuing permission request flow")

        // Check permissions again and continue the flow
        if (!hasOverlayPermission(activity)) {
            Log.d(TAG, "Still need overlay permission")
            requestOverlayPermission(activity)
            return false
        }

        if (!hasUsageStatsPermission(activity)) {
            Log.d(TAG, "Still need usage stats permission")
            requestUsageStatsPermission(activity)
            return false
        }

        // If we have all permissions, start the service
        Log.d(TAG, "All permissions now granted, starting service")
        startMonitoringService(activity)
        isRequestingPermissions = false
        return true
    }

    /**
     * Start the monitoring service with all required permission checks
     */
    fun startMonitoringService(context: Context) {
        // Double-check permissions just to be safe
        if (!hasUsageStatsPermission(context)) {
            Log.e(TAG, "No usage stats permission - can't start service")
            Toast.makeText(context, "Missing usage stats permission", Toast.LENGTH_SHORT).show()
            return
        }

        if (!hasOverlayPermission(context)) {
            Log.e(TAG, "No overlay permission - can't start service")
            Toast.makeText(context, "Missing overlay permission", Toast.LENGTH_SHORT).show()
            return
        }

        // Check if already running
        if (isServiceRunning(context)) {
            Log.d(TAG, "Service already running, not starting again")
            Toast.makeText(context, "Service is already running", Toast.LENGTH_SHORT).show()
            return
        }

        // Start the service
        val serviceIntent = Intent(context, AppMonitoringService::class.java)
        try {
            context.startForegroundService(serviceIntent)
            Log.d(TAG, "Service start requested")
            Toast.makeText(context, "Monitoring service starting...", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start service: ${e.message}")
            Toast.makeText(context, "Failed to start service: ${e.message}", Toast.LENGTH_LONG)
                .show()
        }
    }

    /**
     * Check if the service is currently running
     */
    fun isServiceRunning(context: Context): Boolean {
        // Double-check with system service
        val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Integer.MAX_VALUE)) {
            if (AppMonitoringService::class.java.name == service.service.className) {
                Log.d(TAG, "Service is running according to ActivityManager")
                return true
            }
        }
        return false
    }

    /**
     * Stop the monitoring service
     */
    fun stopMonitoringService(context: Context) {
        val serviceIntent = Intent(context, AppMonitoringService::class.java)
        context.stopService(serviceIntent)
        Log.d(TAG, "Service stop requested")
        Toast.makeText(context, "Stopping monitoring service...", Toast.LENGTH_SHORT).show()
    }

    /**
     * Check if the app has usage stats permission
     */
    fun hasUsageStatsPermission(context: Context): Boolean {
        val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as android.app.AppOpsManager
        val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            appOps.unsafeCheckOpNoThrow(
                android.app.AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(), context.packageName
            )
        } else {
            appOps.checkOpNoThrow(
                android.app.AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(), context.packageName
            )
        }
        return mode == android.app.AppOpsManager.MODE_ALLOWED
    }

    /**
     * Request usage stats permission by opening settings
     */
    private fun requestUsageStatsPermission(context: Context) {
        Toast.makeText(context, "Please grant Usage Access permission", Toast.LENGTH_LONG).show()
        val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    /**
     * Check if the app has overlay permission
     */
    fun hasOverlayPermission(context: Context): Boolean {
        return Settings.canDrawOverlays(context)
    }

    /**
     * Request overlay permission by opening settings
     */
    private fun requestOverlayPermission(context: Context) {
        Toast.makeText(
            context,
            "Please grant Display over other apps permission",
            Toast.LENGTH_LONG
        ).show()
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            android.net.Uri.parse("package:${context.packageName}")
        )
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }
}