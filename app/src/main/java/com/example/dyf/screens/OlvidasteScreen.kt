import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dyf.LoginActivity
import com.example.dyf.data.UserData
import com.example.dyf.data.UserPreferences

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OlvidasteScreen(userPreferences: UserPreferences) {
    var correo by remember { mutableStateOf("") }
    var rut by remember { mutableStateOf("") }

    // Estados para errores de validación
    var correoError by remember { mutableStateOf("El correo no puede estar vacío") }
    var rutError by remember { mutableStateOf("El RUT no puede estar vacío") }
    var password by remember { mutableStateOf<String?>(null) }
    var errorMensaje by remember { mutableStateOf<String?>(null) }
    var userList by remember { mutableStateOf<List<UserData>>(emptyList()) }

    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }
    var dialogMessage by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        userPreferences.userPreferencesFlow.collect { users ->
            userList = users
        }
    }

    // Función de vibración
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

    // Validación de RUT
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

    // Validación de correo
    fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z](.*)([@])(.+)(\\.)(.+)"
        return email.matches(emailRegex.toRegex())
    }

    // Validación del formulario
    fun validateForm(): Boolean {
        var isValid = true

        correoError = when {
            correo.isBlank() -> "El correo no puede estar vacío"
            !isValidEmail(correo) -> "El correo no es válido"
            else -> null.toString()
        }
        rutError = when {
            rut.isBlank() -> "El RUT no puede estar vacío"
            !isValidRUT(rut) -> "El RUT no es válido"
            else -> null.toString()
        }

        isValid = correoError == null && rutError == null

        if (isValid) {
            val user = userList.find { it.correo == correo && it.rut == rut }
            if (user == null) {
                correoError = "Datos erróneos"
                rutError = "Datos erróneos"
                isValid = false
            } else {
                password = user.password
            }
        }

        return isValid
    }

    // Función de recuperación de contraseña
    fun recuperar() {
        if (validateForm()) {
            val usuario = userList.find { it.correo == correo && it.rut == rut }
            if (usuario != null) {
                dialogMessage = "Tu contraseña es: ${usuario.password}"
                showDialog = true
                vibrate(context)
            } else {
                errorMensaje = "Los datos son erróneos"
                dialogMessage = "Los datos son erróneos"
                showDialog = true
                vibrate(context)
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
                .padding(horizontal = 32.dp)
                .padding(top = 64.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Botón Volver
            Box(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.TopStart
            ) {
                IconButton(
                    onClick = {
                        val intent = Intent(context, LoginActivity::class.java)
                        context.startActivity(intent)
                    },
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Volver al Login",
                        tint = Color(0xFFFFC107),
                        modifier = Modifier.size(48.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Ícono Candado
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "Candado",
                tint = Color.Black,
                modifier = Modifier
                    .size(64.dp)
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Título
            Text(
                text = "Recuperar Contraseña",
                style = MaterialTheme.typography.titleLarge.copy(fontSize = 26.sp, fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Campo de Correo Electrónico
            val correoColor by animateColorAsState(targetValue = if (correoError == null) Color(0xFFFFC107) else Color(0xFFFF5449))
            OutlinedTextField(
                value = correo,
                onValueChange = { correo = it },
                label = { Text("Correo Electrónico") },
                isError = correoError != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics { contentDescription = "Campo de entrada de Correo Electrónico" },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = "Icono de Correo Electrónico",
                        tint = correoColor
                    )
                },
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = correoColor,
                    cursorColor = correoColor
                )
            )
            if (correoError != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().padding(top = 5.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Error de Correo",
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

            // Campo de RUT
            val rutColor by animateColorAsState(targetValue = if (rutError == null) Color(0xFFFFC107) else Color(0xFFFF5449))
            OutlinedTextField(
                value = rut,
                onValueChange = { rut = it },
                label = { Text("RUT") },
                isError = rutError != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics { contentDescription = "Campo de entrada del RUT" },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Person,
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

            // Botón para recuperar contraseña
            Button(
                onClick = {
                    if (validateForm()) {
                        recuperar()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFC107)
                )
            ) {
                Text("Recuperar Contraseña", fontSize = 20.sp, color = Color(0xFF000000))
            }
        }

        // Diálogo de confirmación o error
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Resultado", fontSize = 20.sp) },
                text = { Text(dialogMessage, color = Color(0xFF000000), fontSize = 18.sp) },
                confirmButton = {
                    Button(
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFC107)),
                        onClick = {
                            showDialog = false
                            context.startActivity(Intent(context, LoginActivity::class.java))
                        }
                    ) {
                        Text("Aceptar", fontSize = 18.sp, color = Color(0xFF000000))
                    }
                }
            )
        }
    }

}
