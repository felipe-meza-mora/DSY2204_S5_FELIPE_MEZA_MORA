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
    var rutError by remember { mutableStateOf<String?>(null) }
    var correoError by remember { mutableStateOf<String?>(null) }
    var isFormValid by remember { mutableStateOf(false) }
    var password by remember { mutableStateOf<String?>(null) }
    var userList by remember { mutableStateOf<List<UserData>>(emptyList()) }

    var showSuccessDialog by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var successMessage by remember { mutableStateOf("") } // Mensaje de éxito personalizado
    var errorMessage by remember { mutableStateOf("") }   // Mensaje de error personalizado

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        userPreferences.userPreferencesFlow.collect { users ->
            userList = users
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
    fun validateForm() {
        correoError = when {
            correo.isBlank() -> "El correo no puede estar vacío"
            !isValidEmail(correo) -> "El correo no es válido"
            else -> null
        }
        rutError = when {
            rut.isBlank() -> "El RUT no puede estar vacío"
            !isValidRUT(rut) -> "El RUT no es válido"
            else -> null
        }

        isFormValid = correoError == null && rutError == null
    }

    // Función para la vibración
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

    // Función de recuperación de contraseña
    fun recuperar() {
        validateForm()
        if (isFormValid) {
            val usuario = userList.find { it.correo == correo && it.rut == rut }
            if (usuario != null) {
                successMessage = "Tu contraseña es: ${usuario.password}"
                showSuccessDialog = true
                vibrate(context, true)  // Vibración de éxito
            } else {
                errorMessage = "Los datos ingresados no coinciden con ningún usuario registrado."
                showErrorDialog = true
                vibrate(context, false)  // Vibración de error
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
                onValueChange = {
                    correo = it
                    validateForm()
                },
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
                onValueChange = {
                    rut = it
                    validateForm()
                },
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
                onClick = { recuperar() },
                enabled = isFormValid,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isFormValid) Color(0xFFFFC107) else Color(0xFFCCCCCC)
                )
            ) {
                Text("Recuperar Contraseña", fontSize = 20.sp, color = Color(0xFF000000))
            }
        }

        // Diálogo de éxito
        if (showSuccessDialog) {
            AlertDialog(
                onDismissRequest = { showSuccessDialog = false },
                title = { Text("Recuperación Exitosa") },
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
