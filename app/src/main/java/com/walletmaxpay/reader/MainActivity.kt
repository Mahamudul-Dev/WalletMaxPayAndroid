package com.walletmaxpay.reader

import WalletMaxPayAndroidTheme
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Network
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import com.walletmaxpay.reader.screens.ScreenHolder
import com.walletmaxpay.reader.screens.SplashScreen
import com.walletmaxpay.reader.viewModels.MainActivityViewModel
import com.walletmaxpay.reader.viewModels.WebViewViewModel

class MainActivity : ComponentActivity() {


    private lateinit var connectivityReceiver: ConnectivityReceiver
    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var networkCallback: ConnectivityManager.NetworkCallback
    val viewModel: WebViewViewModel = WebViewViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize the ConnectivityReceiver and ConnectivityManager

        // Initialize the ConnectivityReceiver and ConnectivityManager
        connectivityReceiver = ConnectivityReceiver()
        connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(connectivityReceiver, filter)


        MainActivityViewModel().setUpFullScreen(window)
        MainActivityViewModel().askPermission(this, this)
        setContent {
            WalletMaxPayAndroidTheme {
                var splashScreenFinished by remember { mutableStateOf(false) }
                // A surface container using the 'background' color from the theme
                if (splashScreenFinished) {
                    ScreenHolder(viewModel = WebViewViewModel(), context = this)
                } else {
                    SplashScreen(onSplashScreenFinished = { splashScreenFinished = true })
                }
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        // Unregister the BroadcastReceiver when the activity is destroyed
        unregisterReceiver(connectivityReceiver)
        // Unregister the NetworkCallback when the activity is destroyed
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }

    // BroadcastReceiver to listen for network connectivity changes
    inner class ConnectivityReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            // No need to handle connectivity changes here; we'll use NetworkCallback instead
        }
    }


    override fun onResume() {
        super.onResume()
        networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                viewModel.isInternetConnected = true
            }

            override fun onLost(network: Network) {
                viewModel.isInternetConnected = false
            }
        }
        connectivityManager.registerDefaultNetworkCallback(networkCallback)
    }

    override fun onPause() {
        super.onPause()
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }


    // BroadcastReceiver to listen for network connectivity changes


}
