package com.lookmyup.trojanhorse

import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ServiceControlPanel(
    targetApp: String = BuildConfig.TARGET_PACKAGE
) {
    val context = LocalContext.current
    val activity = LocalActivity.current
    val scope = rememberCoroutineScope()
    var isServiceRunning by remember { mutableStateOf(ServiceStarter.isServiceRunning(context)) }
    var hasUsageStats by remember { mutableStateOf(ServiceStarter.hasUsageStatsPermission(context)) }
    var hasOverlay by remember { mutableStateOf(ServiceStarter.hasOverlayPermission(context)) }

    // Periodically check service status and permissions
    LaunchedEffect(Unit) {
        while (true) {
            isServiceRunning = ServiceStarter.isServiceRunning(context)
            hasUsageStats = ServiceStarter.hasUsageStatsPermission(context)
            hasOverlay = ServiceStarter.hasOverlayPermission(context)
            delay(1000) // Check every second
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Trojan Horse Control Panel",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // Permission Status Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Permission Status",
                        style = MaterialTheme.typography.titleLarge
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Usage Stats Permission
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Usage Stats Permission")
                        Text(
                            text = if (hasUsageStats) "GRANTED" else "DENIED",
                            color = if (hasUsageStats)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.error
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Overlay Permission
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Overlay Permission")
                        Text(
                            text = if (hasOverlay) "GRANTED" else "DENIED",
                            color = if (hasOverlay)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.error
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            scope.launch {
                                ServiceStarter.requestPermissionsAndStartService(activity!!)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !hasUsageStats || !hasOverlay
                    ) {
                        Text("Request Missing Permissions")
                    }
                }
            }

            // Service Status Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Service Status",
                        style = MaterialTheme.typography.titleLarge
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (isServiceRunning) "RUNNING" else "STOPPED",
                        style = MaterialTheme.typography.titleLarge,
                        color = if (isServiceRunning)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.error
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(
                            onClick = {
                                scope.launch {
                                    ServiceStarter.startMonitoringService(context)
                                }
                            },
                            enabled = !isServiceRunning && hasUsageStats && hasOverlay
                        ) {
                            Text("Start Service")
                        }

                        Button(
                            onClick = {
                                scope.launch {
                                    ServiceStarter.stopMonitoringService(context)
                                }
                            },
                            enabled = isServiceRunning,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Text("Stop Service")
                        }
                    }
                }
            }

            Text(
                text = "Target app: $targetApp",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 32.dp)
            )
        }
    }
}