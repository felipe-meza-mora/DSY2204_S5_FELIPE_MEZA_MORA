package com.example.dyf.screens

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dyf.LoginActivity
import com.example.dyf.data.UserData
import com.example.dyf.data.UserPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrarseScreen(userPreferences: UserPreferences = UserPreferences(LocalContext.current)) {

    val context = LocalContext.current

    // Textos de entrada
    var rut by remember { mutableStateOf("") }
    var nombreCompleto by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var validarPassword by remember { mutableStateOf("") }
    var recibirNotificaciones by remember { mutableStateOf("Sí") }

    // Estados de error
    var rutError by remember { mutableStateOf<String?>(null) }
    var nombreCompletoError by remember { mutableStateOf<String?>(null) }
    var correoError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var validarPasswordError by remember { mutableStateOf<String?>(null) }

    // Opciones para (DropdownMenu)
    val opcionesNotificaciones = listOf("Sí", "No")
    var expanded by remember { mutableStateOf(false) }

    // Control para los diálogos
    var showSuccessDialog by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var successMessage by remember { mutableStateOf("") } // Mensaje de éxito personalizado
    var errorMessage by remember { mutableStateOf("") }   // Mensaje de error personalizado

    // Estado del botón de registrar
    var isFormValid by remember { mutableStateOf(false) }

    // Función para la vibración al registrar
    fun vibrate(context: Context, isSuccess: Boolean) {
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (vibrator.hasVibrator()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val vibrationEffect = if (isSuccess) {
                    VibrationEffect.createOneShot(300, VibrationEffect.DEFAULT_AMPLITUDE)
                } else {
                    VibrationEffect.createWaveform(longArrayOf(0, 100, 50, 100), -1)
                }
                vibrator.vibrate(vibrationEffect)
            } else {
                if (isSuccess) {
                    vibrator.vibrate(300) // Vibración única de 300ms
                } else {
                    vibrator.vibrate(longArrayOf(0, 100, 50, 100), -1) // Vibración patrón
                }
            }
        }
    }

    // Validador de Rut
    fun isValidRUT(rut: String): Boolean {
        val cleanRut = rut.replace(".", "").replace("-", "")
        if (cleanRut.length < 8) return false

        val rutDigits = cleanRut.dropLast(1)
        val dv = cleanRut.takeLast(1).toUpperCase()

        var sum = 0
        var multiplier = 2
        for (i in rutDigits.reversed()) {
            sum += (i.toString().toInt()) * multiplier
            multiplier = if (multiplier < 7) multiplier + 1 else 2
        }

        val expectedDv = 11 - (sum % 11)
        val expectedDvChar = when (expectedDv) {
            11 -> "0"
            10 -> "K"
            else -> expectedDv.toString()
        }

        return dv == expectedDvChar
    }

    // Función para validar correo electrónico
    fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z](.*)([@])(.+)(\\.)(.+)"
        return email.matches(emailRegex.toRegex())
    }


    // Validar el formulario antes de registrar
    fun validateForm(): Boolean {
        rutError = when {
            rut.isBlank() -> "El RUT no puede estar vacío"
            !isValidRUT(rut) -> "El RUT no es válido"
            else -> null
        }
        nombreCompletoError = if (nombreCompleto.isBlank()) "El nombre completo no puede estar vacío" else null
        correoError = when {
            correo.isBlank() -> "El correo no puede estar vacío"
            !isValidEmail(correo) -> "El correo no tiene un formato válido"
            else -> null
        }
        passwordError = if (password.isBlank()) "La contraseña no puede estar vacía" else null
        validarPasswordError = if (validarPassword.isBlank()) "La validación de contraseña no puede estar vacía"
        else if (password != validarPassword) "Las contraseñas no coinciden" else null

        return rutError == null && nombreCompletoError == null && correoError == null && passwordError == null && validarPasswordError == null
    }

    // Validar el formulario y habilitar o deshabilitar el botón de "Registrarse"
    LaunchedEffect(rut, nombreCompleto, correo, password, validarPassword) {
        isFormValid = validateForm()
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF0F0F0)
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Botón Volver
            IconButton(
                onClick = {
                    val intent = Intent(context, LoginActivity::class.java)
                    context.startActivity(intent)
                },
                modifier = Modifier.align(Alignment.Start).size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Volver al Login",
                    tint = Color(0xFFFFC107),
                    modifier = Modifier.size(48.dp)
                )
            }


            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Registro",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )

            // Campo Rut con retroalimentación visual en base al estado del campo
            val rutColor by animateColorAsState(targetValue = if (rutError == null) Color(0xFFFFC107) else Color(0xFFFF5449))
            OutlinedTextField(
                value = rut,
                onValueChange = {
                    rut = it
                    rutError = null
                },
                label = { Text("Rut") },
                isError = rutError != null,
                modifier = Modifier.fillMaxWidth().semantics { contentDescription = "Campo de entrada de Rut" },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                textStyle = LocalTextStyle.current.copy(fontSize = 18.sp),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.AccountBox,
                        contentDescription = "Icono de RUT",
                        tint = rutColor
                    )
                },
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = rutColor,
                    cursorColor = rutColor
                )
            )
            if (rutError != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().padding(top = 5.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Error de RUT",
                        tint = Color(0xFFFF5449)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = rutError!!,
                        color = Color(0xFFFF5449),
                        fontSize = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Campo Nombre Completo con icono
            OutlinedTextField(
                value = nombreCompleto,
                onValueChange = {
                    nombreCompleto = it
                    nombreCompletoError = null
                },
                label = { Text("Nombre Completo") },
                isError = nombreCompletoError != null,
                modifier = Modifier.fillMaxWidth().semantics { contentDescription = "Campo de entrada de Nombre Completo" },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                textStyle = LocalTextStyle.current.copy(fontSize = 18.sp),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Icono de Nombre Completo"
                    )
                },
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = if (nombreCompletoError != null) Color(0xFFFF5449) else Color(0xFFFFC107),
                    cursorColor = Color(0xFFFFC107)
                )
            )
            if (nombreCompletoError != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().padding(top = 5.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Error de Nombre Completo",
                        tint = Color(0xFFFF5449)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = nombreCompletoError!!,
                        color = Color(0xFFFF5449),
                        fontSize = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Campo Correo Electrónico con icono
            OutlinedTextField(
                value = correo,
                onValueChange = {
                    correo = it
                    correoError = null
                },
                label = { Text("Correo Electrónico") },
                isError = correoError != null,
                modifier = Modifier.fillMaxWidth().semantics { contentDescription = "Campo de entrada de Correo Electrónico" },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                textStyle = LocalTextStyle.current.copy(fontSize = 18.sp),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = "Icono de Correo Electrónico"
                    )
                },
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = if (correoError != null) Color(0xFFFF5449) else Color(0xFFFFC107),
                    cursorColor = Color(0xFFFFC107)
                )
            )
            if (correoError != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().padding(top = 5.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Error de Correo Electrónico",
                        tint = Color(0xFFFF5449)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = correoError!!,
                        color = Color(0xFFFF5449),
                        fontSize = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Campo Contraseña con icono
            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    passwordError = null
                },
                label = { Text("Contraseña") },
                isError = passwordError != null,
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                textStyle = LocalTextStyle.current.copy(fontSize = 18.sp),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Icono de Contraseña"
                    )
                },
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = if (passwordError != null) Color(0xFFFF5449) else Color(0xFFFFC107),
                    cursorColor = Color(0xFFFFC107)
                )
            )
            if (passwordError != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().padding(top = 5.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Error de Contraseña",
                        tint = Color(0xFFFF5449)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = passwordError!!,
                        color = Color(0xFFFF5449),
                        fontSize = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Campo Validar Contraseña con icono
            OutlinedTextField(
                value = validarPassword,
                onValueChange = {
                    validarPassword = it
                    validarPasswordError = null
                },
                label = { Text("Validar Contraseña") },
                isError = validarPasswordError != null,
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                textStyle = LocalTextStyle.current.copy(fontSize = 18.sp),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Icono de Validar Contraseña"
                    )
                },
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = if (validarPasswordError != null) Color(0xFFFF5449) else Color(0xFFFFC107),
                    cursorColor = Color(0xFFFFC107)
                )
            )
            if (validarPasswordError != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().padding(top = 5.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Error de Validar Contraseña",
                        tint = Color(0xFFFF5449)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = validarPasswordError!!,
                        color = Color(0xFFFF5449),
                        fontSize = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ComboBox para "¿Desea recibir notificaciones?"
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = recibirNotificaciones,
                    onValueChange = { },
                    label = { Text("¿Desea recibir notificaciones?", fontSize = 18.sp) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expanded = true },
                    textStyle = LocalTextStyle.current.copy(fontSize = 18.sp),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color(0xFFFFC107),
                        cursorColor = Color(0xFFFFC107)
                    ),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Icono de Notificaciones"
                        )
                    },
                    enabled = false
                )
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier
                        .width(IntrinsicSize.Max)
                ) {
                    opcionesNotificaciones.forEach { opcion ->
                        DropdownMenuItem(
                            text = { Text(opcion, fontSize = 18.sp) },
                            onClick = {
                                recibirNotificaciones = opcion
                                expanded = false
                            },
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón para registrar usuario
            Button(
                onClick = {
                    if (validateForm()) {
                        CoroutineScope(Dispatchers.IO).launch {
                            val currentUsers = userPreferences.getUsers()

                            // Verificar si el RUT o correo ya está registrado
                            val rutExists = currentUsers.any { it.rut == rut }
                            val correoExists = currentUsers.any { it.correo == correo }

                            if (rutExists || correoExists) {
                                errorMessage = when {
                                    rutExists -> "El RUT $rut ya está registrado."
                                    correoExists -> "El correo $correo ya está registrado."
                                    else -> "Error al registrar."
                                }
                                showErrorDialog = true
                                vibrate(context, false)
                            } else {
                                val newUser = UserData(
                                    rut = rut,
                                    nombreCompleto = nombreCompleto,
                                    correo = correo,
                                    password = password,
                                    recibirNotificaciones = recibirNotificaciones
                                )
                                val updatedUsers = currentUsers.toMutableList().apply { add(newUser) }
                                userPreferences.saveUserPreferences(updatedUsers)

                                successMessage = "El usuario $nombreCompleto ha sido registrado exitosamente."
                                showSuccessDialog = true
                                vibrate(context, true)
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFC107)),
                enabled = isFormValid // Deshabilitar si el formulario no es válido
            ) {
                Text("Registrarse", fontSize = 20.sp, color = Color(0xFF000000))
            }
        }

        // Diálogo de éxito
        if (showSuccessDialog) {
            AlertDialog(
                onDismissRequest = { showSuccessDialog = false },
                title = { Text("Registro Exitoso") },
                text = { Text(successMessage, color = Color(0xFF000000), fontSize = 18.sp) },
                confirmButton = {
                    Button(
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFC107)),
                        onClick = {
                            showSuccessDialog = false

                            // Redirigir al LoginActivity
                            val intent = Intent(context, LoginActivity::class.java)
                            context.startActivity(intent)
                        }
                    ) {
                        Text("Aceptar", fontSize = 18.sp, color = Color(0xFF000000))
                    }
                }
            )
        }

        // Diálogo de error
        if (showErrorDialog) {
            AlertDialog(
                onDismissRequest = { showErrorDialog = false },
                title = { Text("Error de Registro") },
                text = { Text(errorMessage, color = Color(0xFF000000), fontSize = 18.sp) },
                confirmButton = {
                    Button(
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5449)),
                        onClick = { showErrorDialog = false }
                    ) {
                        Text("Aceptar", fontSize = 18.sp, color = Color(0xFFFFFFFF))
                    }
                }
            )
        }
    }
}
