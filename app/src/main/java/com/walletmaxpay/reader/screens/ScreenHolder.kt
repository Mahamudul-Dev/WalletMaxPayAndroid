package com.walletmaxpay.reader.screens

import android.Manifest
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.FabPosition
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.walletmaxpay.reader.SmsForegroundService
import com.walletmaxpay.reader.viewModels.WebViewViewModel

@Composable
fun ScreenHolder(
    viewModel: WebViewViewModel,
    modifier: Modifier = Modifier,
    context: Context
) {

    fun isForegroundServiceRunning(context: Context, serviceClass: Class<*>): Boolean {

        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningServices = activityManager.getRunningServices(Integer.MAX_VALUE)

        for (serviceInfo in runningServices) {
            if (serviceInfo.service.className == serviceClass.name) {
                // The service is running
                return true
            }
        }
        // The service is not running
        return false
    }

    var smsReadingEnabled by remember { mutableStateOf(isForegroundServiceRunning(context, SmsForegroundService::class.java)) }

    fun toggleSmsReading(context: Context) {
        val permission = Manifest.permission.RECEIVE_SMS


        if (ContextCompat.checkSelfPermission(context, permission)
            == PackageManager.PERMISSION_GRANTED
        ) {
            if (!smsReadingEnabled) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(
                        Intent(context, SmsForegroundService::class.java)
                    )
                } else {
                    context.startService(Intent(context, SmsForegroundService::class.java))
                }
            } else {
                context.stopService(Intent(context, SmsForegroundService::class.java))

            }
            smsReadingEnabled =
                isForegroundServiceRunning(context, SmsForegroundService::class.java)
            Log.d("SMS_SERVICE", smsReadingEnabled.toString())
        }


        Log.d("Toggle", "Clicked! $smsReadingEnabled")
    }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
        Scaffold(
            floatingActionButton = {
                if (viewModel.showSmsReadButton) {
                    FloatingActionButton(
                        onClick = { toggleSmsReading(context) },
                        modifier = modifier.padding(8.dp)
                    ) {
                        Row {
                            Box(modifier.width(10.dp)) {

                            }
                            Icon(
                                if (smsReadingEnabled) Icons.Default.Close else Icons.Default.PlayArrow,
                                contentDescription = "Read Sms"
                            )
                            Text(text = if (smsReadingEnabled) "Stop Reading" else "Start Reading", style = TextStyle(color = Color.White))

                            Box(Modifier.width(10.dp)) {

                            }
                        }
                    }
                }
            },
            floatingActionButtonPosition = FabPosition.End,
            content = {it
                // You can use an empty Box as a placeholder if you don't have any specific content
                // to display in the main body.
                WalletMaxPayWebView(viewModel = viewModel, webViewId = viewModel.webViewId, onLoadingStateChanged = {isLoading-> viewModel.isLoading = isLoading})

            }

        )
    }
}