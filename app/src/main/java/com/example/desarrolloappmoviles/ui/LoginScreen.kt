package com.example.desarrolloappmoviles.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.desarrolloappmoviles.R
import com.example.desarrolloappmoviles.viewmodel.LoginViewModel

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    abrirRegistro: () -> Unit,
    abrirInicio: () -> Unit,
    modifier: Modifier = Modifier
) {
    // La vista muestra los campos; el ViewModel valida y consulta el repositorio.
    Column(
        modifier.fillMaxSize().imePadding().verticalScroll(rememberScrollState()).padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Image(painterResource(R.drawable.muni_logo), "Logo municipal de referencia", Modifier.size(76.dp))
        Text("CuidApp", style = MaterialTheme.typography.headlineMedium)
        Text("Iniciar sesión", style = MaterialTheme.typography.headlineSmall)
        Text("Ingresa tus datos para continuar.")

        OutlinedTextField(
            value = viewModel.correo,
            onValueChange = {
                viewModel.correo = it
                viewModel.errorCorreo = ""
                viewModel.mensaje = ""
            },
            label = { Text("Correo electrónico") },
            leadingIcon = { Icon(Icons.Filled.Email, "Correo electrónico") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            isError = viewModel.errorCorreo.isNotEmpty(),
            supportingText = { Text(viewModel.errorCorreo) },
            enabled = !viewModel.cargando,
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
            label = { Text("Contraseña") },
            leadingIcon = { Icon(Icons.Filled.Lock, "Contraseña") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = PasswordVisualTransformation(),
            isError = viewModel.errorContrasena.isNotEmpty(),
            supportingText = { Text(viewModel.errorContrasena) },
            enabled = !viewModel.cargando,
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = { viewModel.validarLogin() },
            enabled = !viewModel.cargando,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (viewModel.cargando) {
                CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp, color = LocalContentColor.current)
            } else Text("Ingresar")
        }
        TextButton(onClick = abrirRegistro, enabled = !viewModel.cargando, modifier = Modifier.fillMaxWidth()) {
            Text("¿No tienes cuenta? Regístrate")
        }
    }

    if (viewModel.mensaje.isNotEmpty()) {
        AlertDialog(
            onDismissRequest = { viewModel.mensaje = "" },
            title = { Text(if (viewModel.loginExitoso) "Inicio de sesión" else "No se pudo iniciar sesión") },
            text = { Text(viewModel.mensaje) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.mensaje = ""
                    if (viewModel.loginExitoso) abrirInicio()
                }) {
                    Text(if (viewModel.loginExitoso) "Continuar" else "Aceptar")
                }
            }
        )
    }
}
