package com.walletmaxpay.reader.viewModels

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.content.BroadcastReceiver
import android.os.Build
import android.view.Window
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel


class MainActivityViewModel : ViewModel() {




    companion object {
        private const val REQUEST_SMS_PERMISSION = 1001
    }


    fun setUpFullScreen(window: Window) {
        // Set up full-screen mode for API level 30 and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val insetsController = window.decorView.windowInsetsController
            if (insetsController != null) {
                insetsController.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                insetsController.systemBarsBehavior =
                    WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                insetsController.setSystemBarsAppearance(
                    0,
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                )
            }
            window.setDecorFitsSystemWindows(false)
        } else {
            // For older API levels, use the deprecated flag
            @Suppress("DEPRECATION")
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
    }


    fun askPermission(context: Context, activity: Activity) {

        if (ActivityCompat.checkSelfPermission(
                context, Manifest.permission.RECEIVE_SMS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                activity, arrayOf(Manifest.permission.RECEIVE_SMS), REQUEST_SMS_PERMISSION
            )
        }
    }
}