package com.walletmaxpay.reader.screens

import android.annotation.SuppressLint
import android.util.Log
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.walletmaxpay.reader.viewModels.WebViewViewModel

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WalletMaxPayWebView(viewModel: WebViewViewModel, webViewId: String?, onLoadingStateChanged: (Boolean) -> Unit) {
    val isInternetConnected = remember { mutableStateOf(true) }
    isInternetConnected.value = viewModel.isInternetConnected

    Log.d("Internet",isInternetConnected.value.toString())

    if (isInternetConnected.value){
        Column {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { context ->
                    WebView(context).apply {
                        settings.javaScriptEnabled = true
                        CookieManager.getInstance().setAcceptThirdPartyCookies(this, true)
                        tag = webViewId
                        webViewClient = object : WebViewClient() {
                            override fun shouldInterceptRequest(
                                view: WebView?,
                                request: WebResourceRequest?
                            ): WebResourceResponse? {
                                request?.url?.let { url ->
                                    val cookieValue = CookieManager.getInstance().getCookie(url.toString())
                                    cookieValue?.let { cookie ->
                                        Log.d("Cookie", cookie)
                                        viewModel.onWebViewPageFinished(url.toString(), cookie)
                                    }

                                    Log.d("AppLoaded", "App loaded")
                                }

                                return super.shouldInterceptRequest(view, request)
                            }

                            override fun onPageFinished(view: WebView?, url: String?) {
                                super.onPageFinished(view, url)
                                onLoadingStateChanged(false)
                            }
                        }

                        loadUrl(WebViewViewModel().webViewUrl)
                    }
                })
            if (viewModel.isLoading){
                LoadingScreen()
            }
        }
    } else {
        NoInternetScreen()
    }
}
