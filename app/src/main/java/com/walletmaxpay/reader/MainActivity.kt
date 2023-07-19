package com.walletmaxpay.reader

import WalletMaxPayAndroidTheme
import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import android.webkit.CookieManager
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.FabPosition
import androidx.compose.material.FloatingActionButton
import androidx.compose.runtime.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import com.walletmaxpay.reader.ViewModels.WebViewViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set up full-screen mode for API level 30 and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val insetsController = window.decorView.windowInsetsController
            if (insetsController != null) {
                insetsController.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                insetsController.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                insetsController.setSystemBarsAppearance(0, WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS)
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
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.RECEIVE_SMS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.RECEIVE_SMS), REQUEST_SMS_PERMISSION
            )
        }
        val viewModel = WebViewViewModel()
        setContent {
            WalletMaxPayAndroidTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {

                    buildScreen(viewModel = viewModel, context = LocalContext.current)
                }
            }
        }

    }

    companion object {
        private const val REQUEST_SMS_PERMISSION = 1001
    }
}


@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WalletMaxPayWebView(viewModel: WebViewViewModel , webViewId: String?) {

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
                    }
                    loadUrl(viewModel.webViewUrl)
                }
            })
    }
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
private fun noInternetScreen(viewModel: WebViewViewModel, context: Context) = Scaffold(Modifier.padding(20.dp)) {
    if (viewModel.isInternetAvailable(context = context)){
        Column(verticalArrangement = Arrangement.SpaceAround, horizontalAlignment = Alignment.CenterHorizontally) {
            Image(painter = painterResource(id = R.drawable.no_internet), contentDescription = "No Internet Image")
        }
    }
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun buildScreen (viewModel: WebViewViewModel, context: Context) = Scaffold(

    floatingActionButton = {
        if (viewModel.showSmsReadButton) {
            FloatingActionButton(
                onClick = { viewModel.toggleSmsReading(context) },
                modifier = Modifier.padding(8.dp)
            ) {
                Row {
                    Box(Modifier.width(10.dp)) {

                    }
                    Icon(
                        if (viewModel.smsReadingEnabled) Icons.Default.Close else Icons.Default.PlayArrow,
                        contentDescription = "Read Sms"
                    )
                    Text(text = if (viewModel.smsReadingEnabled) "Stop Reading" else "Start Reading")

                    Box(Modifier.width(10.dp)) {

                    }
                }
            }
        }
    },
    floatingActionButtonPosition = FabPosition.End,
    content = {
        // You can use an empty Box as a placeholder if you don't have any specific content
        // to display in the main body.

        WalletMaxPayWebView(viewModel = viewModel, webViewId = viewModel.webViewId)


    }

)