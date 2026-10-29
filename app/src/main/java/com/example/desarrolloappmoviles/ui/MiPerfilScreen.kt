package com.example.desarrolloappmoviles.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MiPerfilScreen(
    nombreUsuario: String,
    volverInicio: () -> Unit,
    cerrarSesion: () -> Unit,
    modifier: Modifier = Modifier
) {
    var confirmarSalida by remember { mutableStateOf(false) }

    Column(
        modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Mi perfil", style = MaterialTheme.typography.headlineMedium)
        Card(Modifier.fillMaxWidth()) {
            Column(
                Modifier.fillMaxWidth().padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(Icons.Filled.AccountCircle, contentDescription = "Perfil", modifier = Modifier.size(80.dp))
                Text(nombreUsuario.ifBlank { "Usuario de CuidApp" }, style = MaterialTheme.typography.titleLarge)
                Text("Rol: Vecina o vecino")
            }
        }
        Button(onClick = { confirmarSalida = true }, modifier = Modifier.fillMaxWidth()) {
            Text("Cerrar sesión")
        }
        OutlinedButton(onClick = volverInicio, modifier = Modifier.fillMaxWidth()) {
            Text("Volver al inicio")
        }
    }

    if (confirmarSalida) {
        AlertDialog(
            onDismissRequest = { confirmarSalida = false },
            title = { Text("Cerrar sesión") },
            text = { Text("¿Quieres cerrar tu sesión en este dispositivo?") },
            confirmButton = {
                TextButton(onClick = {
                    confirmarSalida = false
                    cerrarSesion()
                }) { Text("Sí, cerrar sesión") }
            },
            dismissButton = {
                TextButton(onClick = { confirmarSalida = false }) { Text("Cancelar") }
            }
        )
    }
}
