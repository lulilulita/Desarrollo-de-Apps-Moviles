package com.example.desarrolloappmoviles.ui

import android.Manifest
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.example.desarrolloappmoviles.R
import com.example.desarrolloappmoviles.viewmodel.IncidenciaViewModel
import java.io.ByteArrayOutputStream
import java.io.File

@Composable
fun NuevaIncidenciaScreen(
    viewModel: IncidenciaViewModel,
    volverLogin: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var fotoSeleccionada by remember { mutableStateOf<Bitmap?>(null) }
    var errorCamara by remember { mutableStateOf("") }

    val takePictureLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { exito ->
        val uri = imageUri

        if (exito && uri != null) {
            try {
                fotoSeleccionada =
                    context.contentResolver.openInputStream(uri)?.use { stream ->
                        BitmapFactory.decodeStream(stream)
                    }

                errorCamara = if (fotoSeleccionada == null) {
                    "No se pudo cargar la foto."
                } else {
                    ""
                }
            } catch (error: Exception) {
                fotoSeleccionada = null
                errorCamara = "No se pudo cargar la foto."
            }
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { permisoConcedido ->
        if (permisoConcedido) {
            try {
                val archivoFoto = File.createTempFile(
                    "incidencia_",
                    ".jpg",
                    context.cacheDir
                )

                val uriFoto = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.provider",
                    archivoFoto
                )

                imageUri = uriFoto
                takePictureLauncher.launch(uriFoto)
            } catch (error: Exception) {
                errorCamara = "No se pudo abrir la cámara."
            }
        } else {
            errorCamara = "Se necesita permiso para usar la cámara."
        }
    }

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
                        text = "Nueva incidencia",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text("Cuéntanos qué ocurre en tu comunidad.")
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

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "Usa una situación inventada y un sector ficticio.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Text(
            text = "Datos de la incidencia",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        SelectorIncidencia(
            titulo = "Categoría",
            valor = viewModel.categoria,
            opciones = viewModel.categorias,
            error = viewModel.errorCategoria,
            alSeleccionar = {
                viewModel.categoria = it
                viewModel.errorCategoria = ""
            }
        )

        OutlinedTextField(
            value = viewModel.descripcion,
            onValueChange = {
                viewModel.descripcion = it
                viewModel.errorDescripcion = ""
            },
            label = { Text("Descripción") },
            placeholder = {
                Text("Ejemplo: luminaria apagada en el sector ficticio A.")
            },
            minLines = 3,
            maxLines = 6,
            isError = viewModel.errorDescripcion.isNotEmpty(),
            supportingText = {
                Text(
                    viewModel.errorDescripcion.ifEmpty {
                        "${viewModel.descripcion.length}/300 caracteres"
                    }
                )
            },
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        )

        SelectorIncidencia(
            titulo = "Prioridad",
            valor = viewModel.prioridad,
            opciones = viewModel.prioridades,
            error = viewModel.errorPrioridad,
            alSeleccionar = {
                viewModel.prioridad = it
                viewModel.errorPrioridad = ""
            }
        )

        SelectorIncidencia(
            titulo = "Sector ficticio",
            valor = viewModel.sector,
            opciones = viewModel.sectores,
            error = viewModel.errorSector,
            alSeleccionar = {
                viewModel.sector = it
                viewModel.errorSector = ""
            }
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.LocationOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Evidencia fotográfica",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Button(
                    onClick = {
                        errorCamara = ""
                        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        if (fotoSeleccionada == null) {
                            "Tomar foto"
                        } else {
                            "Cambiar foto"
                        }
                    )
                }

                fotoSeleccionada?.let { imagen ->
                    Image(
                        bitmap = imagen.asImageBitmap(),
                        contentDescription = "Foto de evidencia de la incidencia",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(14.dp)),
                        contentScale = ContentScale.Crop
                    )
                }

                if (errorCamara.isNotEmpty()) {
                    Text(
                        text = errorCamara,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }

        Button(
            onClick = {
                val fotoBytes = fotoSeleccionada?.let { imagen ->
                    ByteArrayOutputStream().use { salida ->
                        imagen.compress(Bitmap.CompressFormat.JPEG, 70, salida)
                        salida.toByteArray()
                    }
                }

                viewModel.validarIncidencia(fotoBytes)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = "Guardar incidencia",
                style = MaterialTheme.typography.titleMedium
            )
        }

        if (viewModel.errorGuardado.isNotEmpty()) {
            Text(
                text = viewModel.errorGuardado,
                color = MaterialTheme.colorScheme.error
            )
        }

        TextButton(
            onClick = volverLogin,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Volver al inicio")
        }
    }

    if (viewModel.mostrarConfirmacion) {
        AlertDialog(
            onDismissRequest = {
                viewModel.mostrarConfirmacion = false
            },
            title = {
                Text("Incidencia registrada")
            },
            text = {
                Text(
                    "La incidencia se guardó correctamente.\n\n" +
                            "Categoría: ${viewModel.categoria}\n" +
                            "Descripción: ${viewModel.descripcion.trim()}\n" +
                            "Prioridad: ${viewModel.prioridad}\n" +
                            "Sector: ${viewModel.sector}"
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.mostrarConfirmacion = false
                    }
                ) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        viewModel.limpiarFormulario()
                        fotoSeleccionada = null
                        imageUri = null
                        errorCamara = ""
                    }
                ) {
                    Text("Nueva incidencia")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SelectorIncidencia(
    titulo: String,
    valor: String,
    opciones: List<String>,
    error: String,
    alSeleccionar: (String) -> Unit
) {
    var expandido by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expandido,
        onExpandedChange = { expandido = it }
    ) {
        OutlinedTextField(
            value = valor,
            onValueChange = {},
            readOnly = true,
            label = { Text(titulo) },
            placeholder = { Text("Selecciona una opción") },
            isError = error.isNotEmpty(),
            supportingText = {
                if (error.isNotEmpty()) {
                    Text(error)
                }
            },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandido)
            },
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )

        ExposedDropdownMenu(
            expanded = expandido,
            onDismissRequest = { expandido = false }
        ) {
            opciones.forEach { opcion ->
                DropdownMenuItem(
                    text = { Text(opcion) },
                    onClick = {
                        alSeleccionar(opcion)
                        expandido = false
                    }
                )
            }
        }
    }
}