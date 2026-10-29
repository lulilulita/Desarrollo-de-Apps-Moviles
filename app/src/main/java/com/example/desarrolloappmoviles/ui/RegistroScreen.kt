package com.example.desarrolloappmoviles.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.desarrolloappmoviles.R
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
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Image(painterResource(R.drawable.muni_logo), "Logo municipal de referencia", Modifier.size(72.dp))
        Text("Crear cuenta", style = MaterialTheme.typography.headlineSmall)
        Text("Crea tu cuenta para registrar incidencias comunitarias.")

        OutlinedTextField(
            enabled = !viewModel.cargando,
            value = viewModel.nombre,
            onValueChange = {
                viewModel.nombre = it
                viewModel.errorNombre = ""
                viewModel.mensaje = ""
            },
            label = { Text("Nombre") },
            leadingIcon = {
                Icon(Icons.Filled.Person, contentDescription = "Nombre")
            },
            isError = viewModel.errorNombre.isNotEmpty(),
            supportingText = { Text(viewModel.errorNombre) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            enabled = !viewModel.cargando,
            value = viewModel.apellido,
            onValueChange = {
                viewModel.apellido = it
                viewModel.errorApellido = ""
                viewModel.mensaje = ""
            },
            label = { Text("Apellido") },
            leadingIcon = {
                Icon(Icons.Filled.Person, contentDescription = "Apellido")
            },
            isError = viewModel.errorApellido.isNotEmpty(),
            supportingText = { Text(viewModel.errorApellido) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            enabled = !viewModel.cargando,
            value = viewModel.correo,
            onValueChange = {
                viewModel.correo = it
                viewModel.errorCorreo = ""
                viewModel.mensaje = ""
            },
            label = { Text("Correo electrónico") },
            placeholder = { Text("usuario@ejemplo.com") },
            leadingIcon = {
                Icon(Icons.Filled.Email, contentDescription = "Correo electrónico")
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email
            ),
            isError = viewModel.errorCorreo.isNotEmpty(),
            supportingText = { Text(viewModel.errorCorreo) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            enabled = !viewModel.cargando,
            value = viewModel.contrasena,
            onValueChange = {
                viewModel.contrasena = it
                viewModel.errorContrasena = ""
                viewModel.errorConfirmacion = ""
                viewModel.mensaje = ""
            },
            label = { Text("Contraseña") },
            leadingIcon = {
                Icon(Icons.Filled.Lock, contentDescription = "Contraseña")
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password
            ),
            visualTransformation = PasswordVisualTransformation(),
            isError = viewModel.errorContrasena.isNotEmpty(),
            supportingText = { Text(viewModel.errorContrasena) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            enabled = !viewModel.cargando,
            value = viewModel.confirmarContrasena,
            onValueChange = {
                viewModel.confirmarContrasena = it
                viewModel.errorConfirmacion = ""
                viewModel.mensaje = ""
            },
            label = { Text("Confirmar contraseña") },
            leadingIcon = {
                Icon(Icons.Filled.Lock, contentDescription = "Confirmar contraseña")
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password
            ),
            visualTransformation = PasswordVisualTransformation(),
            isError = viewModel.errorConfirmacion.isNotEmpty(),
            supportingText = { Text(viewModel.errorConfirmacion) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = { viewModel.validarRegistro() },
            enabled = !viewModel.cargando,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            if (viewModel.cargando) {
                CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp)
            } else Text(
                text = "Crear cuenta",
                style = MaterialTheme.typography.titleMedium
            )
        }

        AnimatedVisibility(visible = viewModel.mensaje.isNotEmpty()) {
            Text(
                text = viewModel.mensaje,
                color = if (viewModel.registroExitoso) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.error
                }
            )
        }

        TextButton(
            onClick = volverLogin,
            enabled = !viewModel.cargando,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("¿Ya tienes cuenta? Inicia sesión")
        }
    }
}
