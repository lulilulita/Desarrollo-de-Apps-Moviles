package com.example.desarrolloappmoviles.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.desarrolloappmoviles.viewmodel.RegistroViewModel

@Composable
fun RegistroScreen(
    viewModel: RegistroViewModel,
    volverLogin: () -> Unit,
    modifier: Modifier = Modifier
) {
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
            text = "Crear cuenta",
            style = MaterialTheme.typography.headlineSmall
        )

        Text("Completa tus datos de prueba")

        OutlinedTextField(
            value = viewModel.correo,
            onValueChange = {
                viewModel.correo = it
                viewModel.errorCorreo = ""
                viewModel.mensaje = ""
            },
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
                viewModel.errorConfirmacion = ""
                viewModel.mensaje = ""
            },
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

        OutlinedTextField(
            value = viewModel.confirmarContrasena,
            onValueChange = {
                viewModel.confirmarContrasena = it
                viewModel.errorConfirmacion = ""
                viewModel.mensaje = ""
            },
            label = { Text("Confirmar contrasena") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password
            ),
            visualTransformation = PasswordVisualTransformation(),
            isError = viewModel.errorConfirmacion.isNotEmpty(),
            supportingText = {
                Text(viewModel.errorConfirmacion)
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = { viewModel.validarRegistro() },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Validar registro")
        }

        if (viewModel.mensaje.isNotEmpty()) {
            Text(viewModel.mensaje)
        }

        TextButton(
            onClick = volverLogin,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ya tienes cuenta? Inicia sesion")
        }
    }
}