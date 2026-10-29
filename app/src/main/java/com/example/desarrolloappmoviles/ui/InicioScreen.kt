package com.example.desarrolloappmoviles.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.desarrolloappmoviles.R

@Composable
fun InicioScreen(
    nombreUsuario: String,
    abrirNuevaIncidencia: () -> Unit,
    verIncidencias: () -> Unit,
    abrirPerfil: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Image(
                    painter = painterResource(R.drawable.muni_logo),
                    contentDescription = "Logo municipal de referencia",
                    modifier = Modifier.height(80.dp)
                )
                Text("CuidApp", style = MaterialTheme.typography.headlineLarge)
                Text("Hola, ${nombreUsuario.ifBlank { "vecina o vecino" }}",
                    style = MaterialTheme.typography.titleLarge)
            }
        }
        Text("¿Qué necesitas hacer?", style = MaterialTheme.typography.titleLarge)
        Button(onClick = abrirNuevaIncidencia, modifier = Modifier.fillMaxWidth()) {
            Icon(Icons.Filled.Add, contentDescription = null)
            Text("Reportar incidencia")
        }
        OutlinedButton(onClick = verIncidencias, modifier = Modifier.fillMaxWidth()) {
            Text("Mis incidencias y comunidad")
        }
        OutlinedButton(onClick = abrirPerfil, modifier = Modifier.fillMaxWidth()) {
            Text("Mi perfil")
        }
        Text("Usa datos ficticios para esta demostración académica.",
            style = MaterialTheme.typography.bodySmall)
    }
}
