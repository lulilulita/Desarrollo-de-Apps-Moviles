package com.example.desarrolloappmoviles.ui

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.desarrolloappmoviles.model.IncidenciaEntity
import com.example.desarrolloappmoviles.viewmodel.IncidenciaViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DetalleIncidenciaScreen(
    incidencia: IncidenciaEntity,
    viewModel: IncidenciaViewModel,
    volver: () -> Unit,
    modifier: Modifier = Modifier
) {
    var mostrarConfirmacionEliminar by remember {
        mutableStateOf(false)
    }

    val foto = remember(incidencia.id) {
        incidencia.fotoEvidencia?.let { bytes ->
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        }
    }

    val fecha = remember(incidencia.fechaRegistro) {
        SimpleDateFormat(
            "dd/MM/yyyy HH:mm",
            Locale.getDefault()
        ).format(Date(incidencia.fechaRegistro))
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Detalle de incidencia",
            style = MaterialTheme.typography.headlineMedium
        )

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = incidencia.categoria,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text("Descripción: ${incidencia.descripcion}")
                Text("Prioridad: ${incidencia.prioridad}")
                Text("Sector: ${incidencia.sector}")
                Text("Estado: ${incidencia.estado}")
                Text("Responsable: ${incidencia.responsable}")
                Text("Fecha: $fecha")
            }
        }

        if (foto != null) {
            Image(
                bitmap = foto.asImageBitmap(),
                contentDescription = "Foto de evidencia de la incidencia",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
        } else {
            Text("Esta incidencia no tiene una foto adjunta.")
        }

        if (viewModel.errorEliminacion.isNotEmpty()) {
            Text(
                text = viewModel.errorEliminacion,
                color = MaterialTheme.colorScheme.error
            )
        }

        Button(
            onClick = volver,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Volver a incidencias")
        }

        OutlinedButton(
            onClick = { mostrarConfirmacionEliminar = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Eliminar incidencia")
        }
    }

    if (mostrarConfirmacionEliminar) {
        AlertDialog(
            onDismissRequest = {
                mostrarConfirmacionEliminar = false
            },
            title = {
                Text("Eliminar incidencia")
            },
            text = {
                Text("¿Estás segura de que quieres eliminar esta incidencia?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        mostrarConfirmacionEliminar = false
                        viewModel.eliminarIncidencia(incidencia)
                    }
                ) {
                    Text("Sí, eliminar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        mostrarConfirmacionEliminar = false
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    if (viewModel.eliminacionExitosa) {
        AlertDialog(
            onDismissRequest = {
                viewModel.limpiarEstadoEliminacion()
                volver()
            },
            title = {
                Text("Incidencia eliminada")
            },
            text = {
                Text("El registro se eliminó correctamente.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.limpiarEstadoEliminacion()
                        volver()
                    }
                ) {
                    Text("Aceptar")
                }
            }
        )
    }
}