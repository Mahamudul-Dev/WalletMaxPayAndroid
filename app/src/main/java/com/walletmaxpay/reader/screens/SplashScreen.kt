package com.walletmaxpay.reader.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.Colors
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.walletmaxpay.reader.R
import com.walletmaxpay.reader.ui.theme.Purple40
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    modifier: Modifier = Modifier,
    onSplashScreenFinished: () -> Unit

) {

    LaunchedEffect(Unit){
        delay(2000)
        onSplashScreenFinished()
    }

    Surface(
        color = Color.White,
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(painter = painterResource(id = R.drawable.icon), contentDescription = "Wallet Max Pay")
            Box (modifier = Modifier.height(10.dp))
            Text(text = "Wallet Max Pay", fontSize = 19.sp, fontWeight = FontWeight.Bold, color = Purple40)
            Box (modifier = Modifier.height(30.dp))
            LinearProgressIndicator()
        }
    }
}
