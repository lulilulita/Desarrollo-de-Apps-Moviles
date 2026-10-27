package com.example.desarrolloappmoviles.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.desarrolloappmoviles.repository.IncidenciaRepository
import androidx.lifecycle.viewModelScope
import com.example.desarrolloappmoviles.model.IncidenciaEntity
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class IncidenciaViewModel(
    private val repository: IncidenciaRepository
) : ViewModel() {
    val categorias = listOf("Iluminación", "Espacio público", "Convivencia")
    val prioridades = listOf("Baja", "Media", "Alta")
    val sectores = listOf("Sector A", "Sector B", "Sector C")

    val incidencias = repository.observarIncidencias()
    fun observarIncidencia(id: Long) = repository.observarIncidencia(id)
    var categoria by mutableStateOf("")
    var descripcion by mutableStateOf("")
    var prioridad by mutableStateOf("Media")
    var sector by mutableStateOf("")

    var errorCategoria by mutableStateOf("")
    var errorDescripcion by mutableStateOf("")
    var errorPrioridad by mutableStateOf("")
    var errorSector by mutableStateOf("")

    var errorGuardado by mutableStateOf("")

    var errorGestion by mutableStateOf("")
    var mostrarConfirmacion by mutableStateOf(false)

    var eliminacionExitosa by mutableStateOf(false)
    var errorEliminacion by mutableStateOf("")
    var correoUsuarioActual by mutableStateOf("")

    init {
        viewModelScope.launch {
            repository.agregarIncidenciasDeReferenciaSiFaltan()
        }
    }

    fun validarIncidencia(fotoEvidencia: ByteArray? = null){
        errorCategoria = if (categoria !in categorias) "Selecciona una categoría" else ""
        errorDescripcion = when {
            descripcion.isBlank() -> "Describe la incidencia"
            descripcion.trim().length < 10 -> "Escribe al menos 10 caracteres"
            descripcion.length > 300 -> "El máximo es de 300 caracteres"
            else -> ""
        }
        errorPrioridad = if (prioridad !in prioridades) "Selecciona una prioridad" else ""
        errorSector = if (sector !in sectores) "Selecciona un sector ficticio" else ""

        val formularioValido =
            errorCategoria.isEmpty() &&
                    errorDescripcion.isEmpty() &&
                    errorPrioridad.isEmpty() &&
                    errorSector.isEmpty()

        mostrarConfirmacion = false
        errorGuardado = ""

        if (formularioValido) {
            viewModelScope.launch {
                try {
                    val incidencia = IncidenciaEntity(
                        categoria = categoria,
                        descripcion = descripcion.trim(),
                        prioridad = prioridad,
                        sector = sector,
                        fotoEvidencia = fotoEvidencia,
                        correoUsuario = correoUsuarioActual.trim().lowercase()
                    )

                    repository.guardar(incidencia)
                    mostrarConfirmacion = true
                } catch (error: Exception) {
                    errorGuardado = "No se pudo guardar la incidencia."
                }
            }
        }
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

    fun registrarAccionGestion(
        incidencia: IncidenciaEntity,
        accion: String,
        nuevoEstado: String = incidencia.estado,
        nuevoResponsable: String = incidencia.responsable
    ) {
        viewModelScope.launch {
            errorGestion = ""

            try {
                val fecha = SimpleDateFormat(
                    "dd/MM/yyyy HH:mm",
                    Locale.getDefault()
                ).format(Date())

                val nuevaAccion = "$fecha - $accion"
                val historialActual = incidencia.historialAcciones.orEmpty()

                val historialActualizado = if (historialActual.isBlank()) {
                    nuevaAccion
                } else {
                    "$historialActual\n$nuevaAccion"
                }

                repository.actualizar(
                    incidencia.copy(
                        estado = nuevoEstado,
                        responsable = nuevoResponsable,
                        historialAcciones = historialActualizado
                    )
                )
            } catch (error: Exception) {
                errorGestion = "No se pudo actualizar la incidencia."
            }
        }
    }
    fun eliminarIncidencia(incidencia: IncidenciaEntity) {
        eliminacionExitosa = false
        errorEliminacion = ""

        viewModelScope.launch {
            try {
                repository.eliminar(incidencia)
                eliminacionExitosa = true
            } catch (error: Exception) {
                errorEliminacion = "No se pudo eliminar la incidencia."
            }
        }
    }
    fun limpiarEstadoEliminacion() {
        eliminacionExitosa = false
        errorEliminacion = ""
    }


}
