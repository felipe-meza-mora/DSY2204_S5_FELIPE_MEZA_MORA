package com.example.dyf

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.dyf.data.UserPreferences
import com.example.dyf.screens.LoginScreen
import com.example.dyf.ui.theme.DyfTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginActivity : ComponentActivity() {

    private lateinit var userPreferences: UserPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        userPreferences = UserPreferences(this)

        // Obtener y mostrar la lista de usuarios al iniciar la aplicación
        CoroutineScope(Dispatchers.IO).launch {
            val allUsers = userPreferences.getUsers() // Llama a la función getUsers()
            allUsers.forEach { user ->
                Log.d("UsuariosRegistrados", "Nombre: ${user.nombreCompleto}, Correo: ${user.correo}, RUT: ${user.rut}")
            }

                //Función para limpiar la lista de usuarios (activarla cuando sea necesario)
               /*
                CoroutineScope(Dispatchers.IO).launch {
                    userPreferences.clearUsers()  // Llama a clearUsers() para eliminar los usuarios
                    Log.d("UsuariosRegistrados", "Lista de usuarios eliminada.")
                }

                */


        }

    setContent {
    DyfTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                )
                {
                    LoginScreen()
                }
            }
        }
    }
}