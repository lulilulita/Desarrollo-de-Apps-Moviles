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

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    abrirRegistro: () -> Unit,
    abrirIncidencia: () -> Unit,
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
            text = "Iniciar sesión",
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
            enabled = !viewModel.cargando,
            label = { Text("Correo electrónico") },
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
            enabled = !viewModel.cargando,
            label = { Text("Contraseña") },
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
            onClick = { viewModel.validarLogin() },
            enabled = !viewModel.cargando,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            if (viewModel.cargando) {
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
            AlertDialog(
                onDismissRequest = {
                    viewModel.mensaje = ""
                },
                title = {
                    Text(
                        if (viewModel.loginExitoso) {
                            "Inicio de sesión"
                        } else {
                            "No se pudo iniciar sesión"
                        }
                    )
                },
                text = {
                    Text(viewModel.mensaje)
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            val accesoPermitido = viewModel.loginExitoso
                            viewModel.mensaje = ""

                            if (accesoPermitido) {
                                abrirIncidencia()
                            }
                        }
                    ) {
                        Text(
                            if (viewModel.loginExitoso) {
                                "Continuar"
                            } else {
                                "Aceptar"
                            }
                        )
                    }
                }
            )
        }

        TextButton(
            onClick = abrirRegistro,
            enabled = !viewModel.cargando,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("¿No tienes cuenta? Regístrate")
        }
    }
}