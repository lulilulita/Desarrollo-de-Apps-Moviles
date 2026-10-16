package com.example.desarrolloappmoviles.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.desarrolloappmoviles.viewmodel.LoginViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    abrirRegistro: () -> Unit,
    modifier: Modifier = Modifier
) {
    var cargando by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = modifier
            .fillMaxSize()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "CuidApp",
            style = MaterialTheme.typography.headlineLarge
        )

        Text(
            text = "Iniciar sesion",
            style = MaterialTheme.typography.headlineSmall
        )

        Text("Ingresa tus datos para continuar")

        OutlinedTextField(
            value = viewModel.correo,
            onValueChange = {
                viewModel.correo = it
                viewModel.errorCorreo = ""
                viewModel.mensaje = ""
            },
            enabled = !cargando,
            label = { Text("Correo electronico") },
            placeholder = { Text("usuario@ejemplo.com") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email
            ),
            isError = viewModel.errorCorreo.isNotEmpty(),
            supportingText = {
                Text(viewModel.errorCorreo)
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = viewModel.contrasena,
            onValueChange = {
                viewModel.contrasena = it
                viewModel.errorContrasena = ""
                viewModel.mensaje = ""
            },
            enabled = !cargando,
            label = { Text("Contrasena") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password
            ),
            visualTransformation = PasswordVisualTransformation(),
            isError = viewModel.errorContrasena.isNotEmpty(),
            supportingText = {
                Text(viewModel.errorContrasena)
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                viewModel.validarLogin()

                if (
                    viewModel.errorCorreo.isEmpty() &&
                    viewModel.errorContrasena.isEmpty()
                ) {
                    viewModel.mensaje = ""
                    cargando = true

                    coroutineScope.launch {
                        delay(2000)
                        cargando = false
                        viewModel.validarLogin()
                    }
                }
            },
            enabled = !cargando,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            if (cargando) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = LocalContentColor.current
                )
            } else {
                Text("Ingresar")
            }
        }

        if (viewModel.mensaje.isNotEmpty()) {
            Text(viewModel.mensaje)
        }

        TextButton(
            onClick = abrirRegistro,
            enabled = !cargando,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("No tienes cuenta? Registrate")
        }
    }
}