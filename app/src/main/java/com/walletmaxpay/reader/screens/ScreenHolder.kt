package com.walletmaxpay.reader.screens

import android.content.Context
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.walletmaxpay.reader.viewModels.WebViewViewModel

@Composable
fun ScreenHolder(
    viewModel: WebViewViewModel,
    modifier: Modifier = Modifier,
    context: Context
) {
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
        Scaffold(
            floatingActionButton = {
                if (viewModel.showSmsReadButton) {
                    FloatingActionButton(
                        onClick = { viewModel.toggleSmsReading(context) },
                        modifier = modifier.padding(8.dp)
                    ) {
                        Row {
                            Box(modifier.width(10.dp)) {

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
            content = {it
                // You can use an empty Box as a placeholder if you don't have any specific content
                // to display in the main body.
                WalletMaxPayWebView(viewModel = viewModel, webViewId = viewModel.webViewId, onLoadingStateChanged = {isLoading-> viewModel.isLoading = isLoading})

            }

        )
    }
}