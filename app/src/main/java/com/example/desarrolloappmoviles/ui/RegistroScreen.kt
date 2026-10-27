package com.example.desarrolloappmoviles.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
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
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Text(
                        text = "Crear cuenta",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text("Crea tu cuenta para registrar incidencias comunitarias.")
                }

                Image(
                    painter = painterResource(id = R.drawable.muni_logo),
                    contentDescription = "Logo municipal de referencia",
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(14.dp)),
                    contentScale = ContentScale.Fit
                )
            }
        }

        Text(
            text = "Tus datos",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        OutlinedTextField(
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
            supportingText = {
                if (viewModel.errorNombre.isNotEmpty()) {
                    Text(viewModel.errorNombre)
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
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
            supportingText = {
                if (viewModel.errorApellido.isNotEmpty()) {
                    Text(viewModel.errorApellido)
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
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
            supportingText = {
                if (viewModel.errorCorreo.isNotEmpty()) {
                    Text(viewModel.errorCorreo)
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
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
            label = { Text("Contraseña") },
            leadingIcon = {
                Icon(Icons.Filled.Lock, contentDescription = "Contraseña")
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password
            ),
            visualTransformation = PasswordVisualTransformation(),
            isError = viewModel.errorContrasena.isNotEmpty(),
            supportingText = {
                if (viewModel.errorContrasena.isNotEmpty()) {
                    Text(viewModel.errorContrasena)
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
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
            supportingText = {
                if (viewModel.errorConfirmacion.isNotEmpty()) {
                    Text(viewModel.errorConfirmacion)
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                viewModel.validarRegistro()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = "Crear cuenta",
                style = MaterialTheme.typography.titleMedium
            )
        }

        if (viewModel.mensaje.isNotEmpty()) {
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
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("¿Ya tienes cuenta? Inicia sesión")
        }
    }
}