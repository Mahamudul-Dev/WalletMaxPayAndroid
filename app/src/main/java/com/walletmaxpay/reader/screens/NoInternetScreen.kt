package com.walletmaxpay.reader.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.walletmaxpay.reader.R

@Composable
fun NoInternetScreen(
    modifier: Modifier = Modifier
) {
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Image(painter = painterResource(id = R.drawable.no_internet), contentDescription = "No Internet Photo")
    }
}

@Preview(name = "NoInternetScreen", showSystemUi = true)
@Composable
private fun PreviewNoInternetScreen() {
    NoInternetScreen()
}