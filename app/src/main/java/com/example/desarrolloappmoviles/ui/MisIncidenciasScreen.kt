package com.example.desarrolloappmoviles.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.desarrolloappmoviles.viewmodel.IncidenciaViewModel

@Composable
fun MisIncidenciasScreen(
    viewModel: IncidenciaViewModel,
    volverInicio: () -> Unit,
    modifier: Modifier = Modifier
) {
    val incidencias by viewModel.incidencias.collectAsState(initial = emptyList())

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Mis incidencias",
            style = MaterialTheme.typography.headlineMedium
        )

        if (incidencias.isEmpty()) {
            Text("Aún no tienes incidencias registradas.")
        } else {
            incidencias.forEach { incidencia ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = incidencia.categoria,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text("Descripción: ${incidencia.descripcion}")
                        Text("Prioridad: ${incidencia.prioridad}")
                        Text("Sector: ${incidencia.sector}")
                        Text("Estado: ${incidencia.estado}")
                    }
                }
            }
        }

        Button(
            onClick = volverInicio,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Volver al inicio")
        }
    }
}