package com.example.desarrolloappmoviles.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.desarrolloappmoviles.repository.IncidenciaRepository
import androidx.lifecycle.viewModelScope
import com.example.desarrolloappmoviles.model.IncidenciaEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.CancellationException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class IncidenciaViewModel(
    private val repository: IncidenciaRepository
) : ViewModel() {
    val categorias = listOf("Iluminación", "Espacio público", "Convivencia")
    val prioridades = listOf("Baja", "Media", "Alta")
    val sectores = listOf("Sector A", "Sector B", "Sector C")

    // Como en CancionViewModel: leer la lista y recargar después de guardar o actualizar.
    val incidencias = MutableStateFlow<List<IncidenciaEntity>>(emptyList())
    var errorConsulta by mutableStateOf("")
    var eliminandoIncidencia by mutableStateOf(false)
        private set

    init { cargarIncidencias() }

    fun cargarIncidencias() {
        viewModelScope.launch {
            try {
                incidencias.value = repository.obtenerIncidencias()
                errorConsulta = ""
            } catch (cancelacion: CancellationException) {
                throw cancelacion
            } catch (_: Exception) {
                errorConsulta = "No se pudieron cargar las incidencias. Intenta nuevamente."
            }
        }
    }

    fun filtrarIncidenciasPorEstado(estado: String) {
        viewModelScope.launch {
            try { incidencias.value = repository.filtrarPorEstado(estado); errorConsulta = "" }
            catch (cancelacion: CancellationException) { throw cancelacion }
            catch (_: Exception) { errorConsulta = "No se pudieron filtrar las incidencias." }
        }
    }

    fun eliminarIncidencia(id: Long) {
        if (eliminandoIncidencia) return
        eliminandoIncidencia = true
        viewModelScope.launch {
            try {
                repository.eliminar(id)
                incidencias.value = incidencias.value.filterNot { it.id == id }
                errorConsulta = ""
            } catch (cancelacion: CancellationException) {
                throw cancelacion
            } catch (_: Exception) {
                errorConsulta = "No se pudo eliminar la incidencia. Intenta nuevamente."
            } finally {
                eliminandoIncidencia = false
            }
        }
    }

    fun cargarIncidencia(id: Long) {
        viewModelScope.launch {
            try {
                val incidencia = repository.obtenerPorId(id)
                incidencias.value = (incidencias.value.filterNot { it.id == id } + incidencia)
                errorConsulta = ""
            } catch (cancelacion: CancellationException) { throw cancelacion }
            catch (_: Exception) { errorConsulta = "No se pudo cargar el detalle de la incidencia." }
        }
    }
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

    var correoUsuarioActual by mutableStateOf("")
    var fotoEvidencia by mutableStateOf<ByteArray?>(null)
    var latitud by mutableStateOf<Double?>(null)
    var longitud by mutableStateOf<Double?>(null)
    var obteniendoUbicacion by mutableStateOf(false)
    var guardando by mutableStateOf(false)
        private set
    var gestionando by mutableStateOf(false)
        private set

    fun validarIncidencia() {
        if (guardando || mostrarConfirmacion || obteniendoUbicacion) return
        val errores = repository.validarIncidencia(categoria, descripcion, prioridad, sector)
        errorCategoria = errores.categoria
        errorDescripcion = errores.descripcion
        errorPrioridad = errores.prioridad
        errorSector = errores.sector

        mostrarConfirmacion = false
        errorGuardado = ""

        if (!errores.esValido) return
        val incidencia = IncidenciaEntity(
            categoria = categoria,
            descripcion = descripcion.trim(),
            prioridad = prioridad,
            sector = sector,
            fotoEvidencia = fotoEvidencia,
            latitud = latitud,
            longitud = longitud,
            correoUsuario = correoUsuarioActual.trim().lowercase(),
            historialAcciones = "${fechaActual()} - Incidencia registrada"
        )
        guardando = true
        viewModelScope.launch {
            try {
                repository.guardar(incidencia)
                cargarIncidencias()
                mostrarConfirmacion = true
            } catch (cancelacion: CancellationException) {
                throw cancelacion
            } catch (_: Exception) {
                errorGuardado = "No se pudo guardar la incidencia. Intenta nuevamente."
            } finally {
                guardando = false
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
        errorGuardado = ""
        errorGestion = ""
        fotoEvidencia = null
        latitud = null
        longitud = null
    }

    fun asignarInspector(incidencia: IncidenciaEntity) {
        registrarAccionGestion(
            incidencia = incidencia,
            accion = "Incidencia asignada a inspector de prueba",
            nuevoEstado = "En revisión",
            nuevoResponsable = "Inspector municipal de prueba",
            reemplazoCompleto = true
        )
    }

    fun cerrarIncidencia(incidencia: IncidenciaEntity) {
        registrarAccionGestion(
            incidencia = incidencia,
            accion = "Caso cerrado",
            nuevoEstado = "Cerrada",
            nuevoResponsable = incidencia.responsable,
            reemplazoCompleto = false
        )
    }

    private fun registrarAccionGestion(
        incidencia: IncidenciaEntity,
        accion: String,
        nuevoEstado: String,
        nuevoResponsable: String,
        reemplazoCompleto: Boolean
    ) {
        if (gestionando || incidencia.estado == "Cerrada") return
        if (incidencia.estado == nuevoEstado && incidencia.responsable == nuevoResponsable) return
        gestionando = true
        viewModelScope.launch {
            errorGestion = ""

            try {
                val nuevaAccion = "${fechaActual()} - $accion"
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
                    ),
                    reemplazoCompleto
                )
                cargarIncidencias()
            } catch (cancelacion: CancellationException) {
                throw cancelacion
            } catch (_: Exception) {
                errorGestion = "No se pudo actualizar la incidencia."
            } finally {
                gestionando = false
            }
        }
    }
    private fun fechaActual(): String =
        SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
}
