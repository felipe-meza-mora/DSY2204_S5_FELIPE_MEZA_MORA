package com.example.dyf.screens

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.dyf.MenuActivity
import com.example.dyf.OlvidasteActivity
import com.example.dyf.R
import com.example.dyf.RegistrarseActivity
import com.example.dyf.data.UserPreferences

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen() {
    val context = LocalContext.current
    val userPreferences = remember { UserPreferences(context) }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    // Leer datos de usuarios al iniciar la pantalla
    val usersList by userPreferences.userPreferencesFlow.collectAsState(initial = emptyList())

    fun vibrate(context: Context) {
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (vibrator.hasVibrator()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // Para API 26 y superiores
                val vibrationEffect = VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE)
                vibrator.vibrate(vibrationEffect)
            } else {
                // Para versiones anteriores
                @Suppress("DEPRECATION")
                vibrator.vibrate(500) // Duración en milisegundos
            }
        }
    }

    // Validar credenciales
    fun validateCredentials() {
        var valid = true
        if (email.isBlank()) {
            emailError = "El correo no puede estar vacío"
            valid = false
        }
        if (password.isBlank()) {
            passwordError = "La contraseña no puede estar vacía"
            valid = false
        }

        if (valid) {
            val user = usersList.find { it.correo == email && it.password == password }
            if (user != null) {

                val sharedPreferences = context.getSharedPreferences("UserSession", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putString("userName", user.nombreCompleto)
                editor.putString("userEmail", user.correo)
                editor.apply()
                vibrate(context)
                val intent = Intent(context, MenuActivity::class.java)
                context.startActivity(intent)

                //(context as Activity).finish()

            } else {
                vibrate(context)
                emailError = "Credenciales incorrectas"
            }
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF0F0F0)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo
            Image(
                painter = painterResource(id = R.drawable.dyf),
                contentDescription = "Logo DyF",
                modifier = Modifier
                    .height(200.dp)
                    .width(200.dp)
                    .padding(bottom = 32.dp)
            )

            // Texto Correo
            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    emailError = null
                },
                label = { Text("Correo Electrónico") },
                modifier = Modifier.fillMaxWidth(),
                isError = emailError != null,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color(0xFFFFC107),
                    cursorColor = Color(0xFFFFC107)
                )
            )
            if (emailError != null) {
                Text(
                    text = emailError ?: "",
                    color = Color(0xFFBB0000),
                    fontSize = 18.sp,
                    modifier = Modifier.align(Alignment.Start)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Texto Password
            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    passwordError = null
                },
                label = { Text("Contraseña") },
                modifier = Modifier.fillMaxWidth(),
                isError = passwordError != null,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color(0xFFFFC107),
                    cursorColor = Color(0xFFFFC107)
                )
            )
            if (passwordError != null) {
                Text(
                    text = passwordError ?: "",
                    color = Color(0xFFBB0000),
                    fontSize = 18.sp,
                    modifier = Modifier.align(Alignment.Start)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Iniciar Sesión
            Button(
                onClick = { validateCredentials() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFC107)
                )
            ) {
                Text("Iniciar Sesión",fontSize = 18.sp, color = Color(0xFF000000))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Texto para recuperación de contraseña y registro
            Text(
                "¿Olvidaste tu contraseña?",
                color = Color(0xFF969088),
                fontSize = 20.sp,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.clickable {
                    val intent = Intent(context, OlvidasteActivity::class.java)
                    context.startActivity(intent)
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                "Registrarse",
                color = Color(0xFF969088),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable {
                    val intent = Intent(context, RegistrarseActivity::class.java)
                    context.startActivity(intent)
                }
            )
        }
    }
}