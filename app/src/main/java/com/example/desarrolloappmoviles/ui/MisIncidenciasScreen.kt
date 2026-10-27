package com.example.desarrolloappmoviles.ui

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.example.desarrolloappmoviles.model.IncidenciaEntity
import com.example.desarrolloappmoviles.viewmodel.IncidenciaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MisIncidenciasScreen(
    viewModel: IncidenciaViewModel,
    volverInicio: () -> Unit,
    modifier: Modifier = Modifier,
    abrirDetalle: (IncidenciaEntity) -> Unit = {}
) {
    val incidencias by viewModel.incidencias.collectAsState(initial = emptyList())
    var pestanaSeleccionada by remember { mutableStateOf(0) }

    val correoUsuario = viewModel.correoUsuarioActual
        .trim()
        .lowercase()

    val incidenciasPropias = incidencias.filter {
        it.correoUsuario?.trim()?.lowercase() == correoUsuario
    }

    val incidenciasOtrosSectores = incidencias.filter {
        it.correoUsuario?.trim()?.lowercase() != correoUsuario
    }

    val incidenciasVisibles = if (pestanaSeleccionada == 0) {
        incidenciasPropias
    } else {
        incidenciasOtrosSectores
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Incidencias comunitarias",
            style = MaterialTheme.typography.headlineMedium
        )

        TabRow(selectedTabIndex = pestanaSeleccionada) {
            Tab(
                selected = pestanaSeleccionada == 0,
                onClick = { pestanaSeleccionada = 0 },
                text = { Text("Mis incidencias") }
            )
            Tab(
                selected = pestanaSeleccionada == 1,
                onClick = { pestanaSeleccionada = 1 },
                text = { Text("Otros sectores") }
            )
        }

        if (incidenciasVisibles.isEmpty()) {
            Text(
                if (pestanaSeleccionada == 0) {
                    "Aún no has registrado incidencias."
                } else {
                    "Aún no hay incidencias en otros sectores."
                }
            )
        } else {
            incidenciasVisibles.forEach { incidencia ->
                IncidenciaCard(
                    incidencia = incidencia,
                    alTocar = { abrirDetalle(incidencia) }
                )
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

@Composable
private fun IncidenciaCard(
    incidencia: IncidenciaEntity,
    alTocar: () -> Unit
) {
    val foto = remember(incidencia.id) {
        incidencia.fotoEvidencia?.let { bytes ->
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = alTocar)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (foto != null) {
                Image(
                    bitmap = foto.asImageBitmap(),
                    contentDescription = "Foto de la incidencia",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            Text(
                text = incidencia.categoria,
                style = MaterialTheme.typography.titleMedium
            )
            Text("Sector: ${incidencia.sector}")
            Text("Estado: ${incidencia.estado}")
            Text("Toca para ver los detalles")
        }
    }
}