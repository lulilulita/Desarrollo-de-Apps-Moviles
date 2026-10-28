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
import com.example.desarrolloappmoviles.viewmodel.IncidenciaViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DetalleIncidenciaScreen(
    incidenciaId: Long,
    viewModel: IncidenciaViewModel,
    volver: () -> Unit,
    modifier: Modifier = Modifier
) {
    val incidencias by viewModel.incidencias.collectAsState()
    val incidencia = incidencias.find { it.id == incidenciaId }
    if (incidencia == null) {
        Column(modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(viewModel.errorConsulta.ifEmpty { "No se encontró la incidencia." })
            OutlinedButton(onClick = volver) { Text("Volver a incidencias") }
        }
        return
    }

    var mostrarConfirmacionCerrar by remember {
        mutableStateOf(false)
    }

    val foto = remember(
        incidencia.id,
        incidencia.fotoEvidencia
    ) {
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

    val estadoNormalizado = incidencia.estado.trim().lowercase()

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
                    text = incidencia.categoria,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Surface(
                    color = colorEstado,
                    shape = RoundedCornerShape(50)
                ) {
                    Text(
                        text = "Estado: ${incidencia.estado}",
                        color = colorTextoEstado,
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(
                            horizontal = 14.dp,
                            vertical = 7.dp
                        )
                    )
                }

                Text(
                    text = incidencia.descripcion,
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
                    valor = incidencia.sector
                )
                DatoIncidencia(
                    icono = Icons.Filled.LocationOn,
                    etiqueta = "Ubicación",
                    valor = if (incidencia.latitud != null && incidencia.longitud != null)
                        "Latitud: ${incidencia.latitud}\nLongitud: ${incidencia.longitud}"
                    else "Sin ubicación adjunta"
                )
                DatoIncidencia(
                    icono = Icons.Filled.Info,
                    etiqueta = "Prioridad",
                    valor = incidencia.prioridad
                )
                DatoIncidencia(
                    icono = Icons.Filled.Person,
                    etiqueta = "Responsable",
                    valor = incidencia.responsable
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
                    text = incidencia.historialAcciones
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
                    text = "Gestión municipal simulada",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Button(
                    onClick = {
                        viewModel.asignarInspector(incidencia)
                    },
                    enabled = incidencia.estado != "Cerrada" && !viewModel.gestionando,
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
                    enabled = incidencia.estado != "Cerrada" && !viewModel.gestionando,
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

        OutlinedButton(
            onClick = volver,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text("Volver a incidencias")
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
                        viewModel.cerrarIncidencia(incidencia)
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