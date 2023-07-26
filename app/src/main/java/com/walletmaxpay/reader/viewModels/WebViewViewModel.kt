package com.walletmaxpay.reader.viewModels


import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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


    fun onWebViewPageFinished(url: String, cookieValue: String?) {
        webViewUrl = url
        showSmsReadButton = url.contains("user")

        Log.d("WebPageFinished", cookieValue ?: "N/A")
        Log.d("URL", url)
        Log.d("Button", showSmsReadButton.toString())

    }
}