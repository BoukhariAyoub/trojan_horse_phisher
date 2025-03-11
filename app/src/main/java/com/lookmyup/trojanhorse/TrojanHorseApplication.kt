package com.lookmyup.trojanhorse

import android.app.Application
import android.content.Intent
import android.os.Build
import android.util.Log

class TrojanHorseApplication : Application() {
    private val TAG = "TrojanHorse"

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Application onCreate - initializing app")
        
        // Start the monitoring service automatically
        try {
            // Check if we have necessary permissions before starting
            if (ServiceStarter.hasUsageStatsPermission(this) && 
                ServiceStarter.hasOverlayPermission(this)) {
                
                Log.d(TAG, "Auto-starting monitoring service")
                val serviceIntent = Intent(this, AppMonitoringService::class.java)

                startForegroundService(serviceIntent)
            } else {
                Log.d(TAG, "Required permissions not granted - service will not auto-start")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to auto-start service: ${e.message}")
        }
    }
}