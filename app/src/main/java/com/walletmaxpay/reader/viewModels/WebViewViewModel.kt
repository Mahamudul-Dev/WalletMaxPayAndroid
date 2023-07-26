package com.walletmaxpay.reader.viewModels

import com.walletmaxpay.reader.SmsForegroundService
import android.Manifest
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.Build
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel

class WebViewViewModel : ViewModel() {

    var webViewUrl by mutableStateOf("https://dashboard.walletmaxpay.com/home")
        private set

    // Loading state of the WebView
    private val _isLoading: MutableState<Boolean> = mutableStateOf(false)
    var isLoading: Boolean
        get() = _isLoading.value
        set(value) {
            _isLoading.value = value
        }

    // Network connectivity state
    private val _isInternetConnected: MutableState<Boolean> = mutableStateOf(true)
    var isInternetConnected: Boolean
        get() = _isInternetConnected.value
        set(value) {
            _isInternetConnected.value = value
        }

    var webViewId by mutableStateOf<String?>(null)

    var showSmsReadButton by mutableStateOf(false)
        private set

    var smsReadingEnabled by mutableStateOf(false)
        private set




    fun onWebViewPageFinished(url: String, cookieValue: String?) {
        webViewUrl = url
        showSmsReadButton = url.contains("user")

        Log.d("WebPageFinished", cookieValue ?: "N/A")
        Log.d("URL", url)
        Log.d("Button", showSmsReadButton.toString())

    }

    private fun isForegroundServiceRunning(context: Context, serviceClass: Class<*>): Boolean {

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

    fun toggleSmsReading(context: Context) {
        val permission = Manifest.permission.RECEIVE_SMS
        smsReadingEnabled = isForegroundServiceRunning(context, SmsForegroundService::class.java)

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


}