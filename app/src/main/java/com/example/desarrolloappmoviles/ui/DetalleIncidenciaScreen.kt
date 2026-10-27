package com.example.desarrolloappmoviles.ui

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
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

    val estadoNormalizado = datosIncidencia.estado.trim().lowercase()

    val colorEstado = when (estadoNormalizado) {
        "cerrada" -> MaterialTheme.colorScheme.tertiaryContainer
        "en revisión" -> MaterialTheme.colorScheme.secondaryContainer
        else -> MaterialTheme.colorScheme.primaryContainer
    }

    val colorTextoEstado = when (estadoNormalizado) {
        "cerrada" -> MaterialTheme.colorScheme.onTertiaryContainer
        "en revisión" -> MaterialTheme.colorScheme.onSecondaryContainer
        else -> MaterialTheme.colorScheme.onPrimaryContainer
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Detalle de incidencia",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = datosIncidencia.categoria,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Surface(
                    color = colorEstado,
                    shape = RoundedCornerShape(50)
                ) {
                    Text(
                        text = "Estado: ${datosIncidencia.estado}",
                        color = colorTextoEstado,
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(
                            horizontal = 14.dp,
                            vertical = 7.dp
                        )
                    )
                }

                Text(
                    text = datosIncidencia.descripcion,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        if (foto != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp)
            ) {
                Image(
                    bitmap = foto.asImageBitmap(),
                    contentDescription = "Foto de evidencia de la incidencia",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .clip(RoundedCornerShape(20.dp)),
                    contentScale = ContentScale.Crop
                )
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Antecedentes",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                DatoIncidencia(
                    icono = Icons.Filled.LocationOn,
                    etiqueta = "Sector",
                    valor = datosIncidencia.sector
                )
                DatoIncidencia(
                    icono = Icons.Filled.Info,
                    etiqueta = "Prioridad",
                    valor = datosIncidencia.prioridad
                )
                DatoIncidencia(
                    icono = Icons.Filled.Person,
                    etiqueta = "Responsable",
                    valor = datosIncidencia.responsable
                )
                Text(
                    text = "Fecha de registro: $fecha",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Historial de acciones",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = datosIncidencia.historialAcciones
                        ?.takeIf { it.isNotBlank() }
                        ?: "Aún no hay acciones registradas."
                )
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Gestión municipal",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
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
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text("Asignar inspector")
                }

                OutlinedButton(
                    onClick = {
                        mostrarConfirmacionCerrar = true
                    },
                    enabled = datosIncidencia.estado != "Cerrada",
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 8.dp)
                    )
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

        OutlinedButton(
            onClick = volver,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text("Volver a incidencias")
        }

        TextButton(
            onClick = {
                mostrarConfirmacionEliminar = true
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Filled.Delete,
                contentDescription = null,
                modifier = Modifier.padding(end = 8.dp)
            )
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

@Composable
private fun DatoIncidencia(
    icono: ImageVector,
    etiqueta: String,
    valor: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(
            imageVector = icono,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(22.dp)
        )
        Column {
            Text(
                text = etiqueta,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = valor,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}