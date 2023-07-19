package com.walletmaxpay.reader.ViewModels

import com.walletmaxpay.reader.SmsForegroundService
import android.Manifest
import android.app.ActivityManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.Build
import android.util.Log
import android.webkit.WebView
import androidx.activity.OnBackPressedCallback
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel

class WebViewViewModel : ViewModel() {

    var webViewUrl by mutableStateOf("https://walletmaxpay.com")
        private set

    var webViewId by mutableStateOf<String?>(null)

    var showSmsReadButton by mutableStateOf(false)
        private set

    var smsReadingEnabled by mutableStateOf(false)
        private set

    fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetworkInfo
        return activeNetwork?.isConnectedOrConnecting == true
    }


    fun onWebViewPageFinished(url: String, cookieValue: String?) {
        webViewUrl = url
        showSmsReadButton = cookieValue?.contains("sessionData") == true
        Log.d("WebPageFinished", cookieValue ?: "N/A")
        Log.d("Button", showSmsReadButton.toString())

    }

    private fun isForegroundServiceRunning(context: Context, serviceClass: Class<*>): Boolean {
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q){
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
//        } else {
//            val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
//            val runningProcesses = activityManager.runningAppProcesses
//
//            for (processInfo in runningProcesses) {
//                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
//                    // Check if the processInfo.processName matches the service's process name
//                    val packageName = context.packageName
//                    if (processInfo.processName == "$packageName:${serviceClass.simpleName}") {
//                        // The service is running
//                        return true
//                    }
//                }
//            }
//
//            // The service is not running
//            return false
//        }
    }

    fun toggleSmsReading(context: Context) {
        val permission = Manifest.permission.RECEIVE_SMS
        smsReadingEnabled = isForegroundServiceRunning(context, SmsForegroundService::class.java)

        if (ContextCompat.checkSelfPermission(context, permission)
            == PackageManager.PERMISSION_GRANTED
        ) {
            if (!smsReadingEnabled) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                    context.startForegroundService(Intent(context, SmsForegroundService::class.java))
                } else {
                    context.startService(Intent(context, SmsForegroundService::class.java))
                }
            } else {
                context.stopService(Intent(context, SmsForegroundService::class.java))

            }
            smsReadingEnabled = isForegroundServiceRunning(context, SmsForegroundService::class.java)
        }


        Log.d("Toggle","Clicked! $smsReadingEnabled")
    }


}