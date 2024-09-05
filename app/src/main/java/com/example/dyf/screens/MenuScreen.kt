package com.example.dyf.screens

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dyf.LoginActivity
import com.example.dyf.R
import com.example.dyf.data.UserPreferences

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuScreen(userPreferences: UserPreferences) {
    var expanded by remember { mutableStateOf(false) }
    var userName by remember { mutableStateOf("") }


    val usersList by userPreferences.userPreferencesFlow.collectAsState(initial = emptyList())

    val context = LocalContext.current

    fun vibrate(context: Context) {
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (vibrator.hasVibrator()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                val vibrationEffect = VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE)
                vibrator.vibrate(vibrationEffect)
            } else {

                @Suppress("DEPRECATION")
                vibrator.vibrate(500)
            }
        }
    }

    LaunchedEffect(usersList) {

        val loggedInUser = usersList.firstOrNull()
        userName = loggedInUser?.nombreCompleto ?: "Usuario"
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF0F0F0)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color(0xFFFFC107))
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Hola, $userName",
                        fontSize = 18.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Normal
                    )


                    Box(
                        modifier = Modifier
                            .clickable { expanded = !expanded }
                    ) {
                        Text(
                            text = "Menu",
                            fontSize = 18.sp,
                            color = Color.Black,
                            fontWeight = FontWeight.Normal
                        )


                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier.background(Color(0xFFFFC107))
                        ) {
                            DropdownMenuItem(
                                text = { Text("Cerrar sesión") },
                                onClick = {
                                    vibrate(context)
                                    (context as? ComponentActivity)?.finishAffinity()
                                }
                            )
                        }
                    }
                }
            }


            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.dyf),
                    contentDescription = "Logo DyF",
                    modifier = Modifier
                        .size(150.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Pronto más novedades",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Black,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    }
}