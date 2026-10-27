package com.example.desarrolloappmoviles.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun InicioScreen(
    abrirNuevaIncidencia: () -> Unit,
    verIncidencias: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "CuidApp",
            style = MaterialTheme.typography.headlineLarge
        )

        Text(
            text = "Seguridad de tu comunidad",
            style = MaterialTheme.typography.headlineSmall
        )

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Mapa comunitario",
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("Aquí podrás revisar incidencias en sectores de referencia.")
                Spacer(modifier = Modifier.height(12.dp))
                Text(" Sectores comunitarios")
            }
        }

        Button(
            onClick = abrirNuevaIncidencia,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Reportar incidencia")
        }

        OutlinedButton(
            onClick = verIncidencias,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Mis incidencias")
        }
    }
}