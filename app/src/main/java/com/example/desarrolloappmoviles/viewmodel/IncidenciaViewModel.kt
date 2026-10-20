package com.example.desarrolloappmoviles.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class IncidenciaViewModel : ViewModel() {
    val categorias = listOf("Iluminación", "Espacio público", "Convivencia")
    val prioridades = listOf("Baja", "Media", "Alta")
    val sectores = listOf("Sector A", "Sector B", "Sector C")

    var categoria by mutableStateOf("")
    var descripcion by mutableStateOf("")
    var prioridad by mutableStateOf("Media")
    var sector by mutableStateOf("")

    var errorCategoria by mutableStateOf("")
    var errorDescripcion by mutableStateOf("")
    var errorPrioridad by mutableStateOf("")
    var errorSector by mutableStateOf("")
    var mostrarConfirmacion by mutableStateOf(false)

    fun validarIncidencia() {
        errorCategoria = if (categoria !in categorias) "Selecciona una categoría" else ""
        errorDescripcion = when {
            descripcion.isBlank() -> "Describe la incidencia"
            descripcion.trim().length < 10 -> "Escribe al menos 10 caracteres"
            descripcion.length > 300 -> "El máximo es de 300 caracteres"
            else -> ""
        }
        errorPrioridad = if (prioridad !in prioridades) "Selecciona una prioridad" else ""
        errorSector = if (sector !in sectores) "Selecciona un sector ficticio" else ""

        // Solo validamos el formulario: aún no guardamos ni enviamos datos.
        mostrarConfirmacion = errorCategoria.isEmpty() && errorDescripcion.isEmpty() &&
            errorPrioridad.isEmpty() && errorSector.isEmpty()
    }

    fun limpiarFormulario() {
        categoria = ""
        descripcion = ""
        prioridad = "Media"
        sector = ""
        errorCategoria = ""
        errorDescripcion = ""
        errorPrioridad = ""
        errorSector = ""
        mostrarConfirmacion = false
    }
}
