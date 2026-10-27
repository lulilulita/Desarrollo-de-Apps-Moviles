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
import androidx.compose.runtime.collectAsState
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
    val incidenciaObservada by viewModel
        .observarIncidencia(incidencia.id)
        .collectAsState(initial = incidencia)

    val datosIncidencia = incidenciaObservada ?: incidencia

    var mostrarConfirmacionEliminar by remember {
        mutableStateOf(false)
    }
    var mostrarConfirmacionCerrar by remember {
        mutableStateOf(false)
    }

    val foto = remember(
        datosIncidencia.id,
        datosIncidencia.fotoEvidencia
    ) {
        datosIncidencia.fotoEvidencia?.let { bytes ->
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        }
    }

    val fecha = remember(datosIncidencia.fechaRegistro) {
        SimpleDateFormat(
            "dd/MM/yyyy HH:mm",
            Locale.getDefault()
        ).format(Date(datosIncidencia.fechaRegistro))
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
                    text = datosIncidencia.categoria,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text("Descripción: ${datosIncidencia.descripcion}")
                Text("Prioridad: ${datosIncidencia.prioridad}")
                Text("Sector: ${datosIncidencia.sector}")
                Text("Estado: ${datosIncidencia.estado}")
                Text("Responsable: ${datosIncidencia.responsable}")
                Text("Fecha: $fecha")

                Text(
                    text = "Historial de acciones",
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = datosIncidencia.historialAcciones
                        ?.takeIf { it.isNotBlank() }
                        ?: "Aún no hay acciones registradas."
                )
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

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Gestión municipal",
                    style = MaterialTheme.typography.titleMedium
                )

                Button(
                    onClick = {
                        viewModel.registrarAccionGestion(
                            incidencia = datosIncidencia,
                            accion = "Incidencia asignada a Inspector municipal de prueba",
                            nuevoEstado = "En revisión",
                            nuevoResponsable = "Inspector municipal de prueba"
                        )
                    },
                    enabled = datosIncidencia.estado != "Cerrada",
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Asignar inspector")
                }

                OutlinedButton(
                    onClick = {
                        mostrarConfirmacionCerrar = true
                    },
                    enabled = datosIncidencia.estado != "Cerrada",
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cerrar caso")
                }

                if (viewModel.errorGestion.isNotEmpty()) {
                    Text(
                        text = viewModel.errorGestion,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
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
            onClick = {
                mostrarConfirmacionEliminar = true
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Eliminar incidencia")
        }
    }

    if (mostrarConfirmacionCerrar) {
        AlertDialog(
            onDismissRequest = {
                mostrarConfirmacionCerrar = false
            },
            title = {
                Text("Cerrar caso")
            },
            text = {
                Text(
                    "¿Confirmas que esta incidencia fue resuelta y quieres cerrar el caso?"
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        mostrarConfirmacionCerrar = false
                        viewModel.registrarAccionGestion(
                            incidencia = datosIncidencia,
                            accion = "Caso cerrado",
                            nuevoEstado = "Cerrada"
                        )
                    }
                ) {
                    Text("Sí, cerrar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        mostrarConfirmacionCerrar = false
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
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
                        viewModel.eliminarIncidencia(datosIncidencia)
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