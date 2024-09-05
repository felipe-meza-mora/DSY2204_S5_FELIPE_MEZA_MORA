package com.example.dyf.screens

import android.content.Intent
import android.view.animation.OvershootInterpolator
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.example.dyf.LoginActivity
import com.example.dyf.R
import kotlinx.coroutines.delay



@Composable
fun SplashScreen(){
    val context = LocalContext.current
    val color = Color(0xFFF0F0F0)
    val scale = remember {
        androidx.compose.animation.core.Animatable(0f)
    }

    LaunchedEffect(key1 = true) {
        scale.animateTo(
            targetValue = 0.9f,
            animationSpec = tween(
                durationMillis = 800,
                easing = {
                    OvershootInterpolator(8f)
                        .getInterpolation(it)
                }
            )
        )
        delay(2000L)
        val intent = Intent(
            context,
            LoginActivity::class.java
        )
        context.startActivity(intent)

    }
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.scale(scale.value).fillMaxSize().background(color= color )

    ) {
       Image(
           painter = painterResource(id = R.drawable.dyf),
           contentDescription = "Logo DyF"
       )
       Text(
           text = "Bienvenidos",
           style = MaterialTheme.typography.titleLarge,
           color = MaterialTheme.colorScheme.onSecondaryContainer
       )
        /* OutlinedButton(onClick = { }) {
          Text(
              text = "Continuar",
              style = MaterialTheme.typography.bodyLarge,
              color = MaterialTheme.colorScheme.onSecondaryContainer
          )
        } */
    }

}
