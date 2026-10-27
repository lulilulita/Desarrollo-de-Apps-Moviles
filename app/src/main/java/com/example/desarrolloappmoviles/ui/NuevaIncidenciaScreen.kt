package com.example.desarrolloappmoviles.ui

import android.Manifest
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
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
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("CuidApp", style = MaterialTheme.typography.headlineLarge)
        Text("Nueva incidencia", style = MaterialTheme.typography.headlineSmall)
        Text("Usa una situación inventada y un sector ficticio.")

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

        Button(
            onClick = {
                errorCamara = ""
                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
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
                    .height(200.dp),
                contentScale = ContentScale.Crop
            )
        }

        if (errorCamara.isNotEmpty()) {
            Text(
                text = errorCamara,
                color = MaterialTheme.colorScheme.error
            )
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
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar incidencia")
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
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
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