package com.example.desarrolloappmoviles.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.desarrolloappmoviles.viewmodel.IncidenciaViewModel

@Composable
fun NuevaIncidenciaScreen(
    viewModel: IncidenciaViewModel,
    volverLogin: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize().imePadding()
            .verticalScroll(rememberScrollState()).padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("CuidApp", style = MaterialTheme.typography.headlineLarge)
        Text("Nueva incidencia", style = MaterialTheme.typography.headlineSmall)
        Text("Usa una situación inventada y un sector ficticio.")

        SelectorIncidencia(
            titulo = "Categoría", valor = viewModel.categoria,
            opciones = viewModel.categorias, error = viewModel.errorCategoria,
            alSeleccionar = { viewModel.categoria = it; viewModel.errorCategoria = "" }
        )

        OutlinedTextField(
            value = viewModel.descripcion,
            onValueChange = { viewModel.descripcion = it; viewModel.errorDescripcion = "" },
            label = { Text("Descripción") },
            placeholder = { Text("Ejemplo: luminaria apagada en el sector ficticio A.") },
            minLines = 3,
            maxLines = 6,
            isError = viewModel.errorDescripcion.isNotEmpty(),
            supportingText = {
                Text(viewModel.errorDescripcion.ifEmpty { "${viewModel.descripcion.length}/300 caracteres" })
            },
            modifier = Modifier.fillMaxWidth()
        )

        SelectorIncidencia(
            titulo = "Prioridad", valor = viewModel.prioridad,
            opciones = viewModel.prioridades, error = viewModel.errorPrioridad,
            alSeleccionar = { viewModel.prioridad = it; viewModel.errorPrioridad = "" }
        )
        SelectorIncidencia(
            titulo = "Sector ficticio", valor = viewModel.sector,
            opciones = viewModel.sectores, error = viewModel.errorSector,
            alSeleccionar = { viewModel.sector = it; viewModel.errorSector = "" }
        )

        Text("Prueba de formulario: todavía no guarda incidencias. Foto y ubicación quedan para otra etapa.",
            style = MaterialTheme.typography.bodySmall)
        Button(onClick = { viewModel.validarIncidencia() }, modifier = Modifier.fillMaxWidth()) {
            Text("Validar incidencia")
        }
        TextButton(onClick = volverLogin, modifier = Modifier.fillMaxWidth()) { Text("Volver al login") }
    }

    if (viewModel.mostrarConfirmacion) {
        AlertDialog(
            onDismissRequest = { viewModel.mostrarConfirmacion = false },
            title = { Text("Formulario válido") },
            text = {
                Text("Categoría: ${viewModel.categoria}\nPrioridad: ${viewModel.prioridad}\nSector: ${viewModel.sector}\n\nEs una simulación. No se guardó ni envió la incidencia.")
            },
            confirmButton = {
                TextButton(onClick = { viewModel.mostrarConfirmacion = false }) { Text("Aceptar") }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.limpiarFormulario() }) { Text("Nueva prueba") }
            }
        )
    }
}

// El mismo selector se reutiliza para los tres campos, como los controles de Inputs del profesor.
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
    ExposedDropdownMenuBox(expanded = expandido, onExpandedChange = { expandido = it }) {
        OutlinedTextField(
            value = valor,
            onValueChange = {},
            readOnly = true,
            label = { Text(titulo) },
            placeholder = { Text("Selecciona una opción") },
            isError = error.isNotEmpty(),
            supportingText = { if (error.isNotEmpty()) Text(error) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandido) },
            modifier = Modifier.fillMaxWidth().menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
        )
        ExposedDropdownMenu(expanded = expandido, onDismissRequest = { expandido = false }) {
            opciones.forEach { opcion ->
                DropdownMenuItem(text = { Text(opcion) }, onClick = {
                    alSeleccionar(opcion)
                    expandido = false
                })
            }
        }
    }
}
