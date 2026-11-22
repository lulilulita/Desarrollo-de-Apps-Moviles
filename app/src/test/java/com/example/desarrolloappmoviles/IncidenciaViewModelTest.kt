package com.example.desarrolloappmoviles

import com.example.desarrolloappmoviles.viewmodel.IncidenciaViewModel
import com.example.desarrolloappmoviles.model.IncidenciaDao
import com.example.desarrolloappmoviles.model.IncidenciaEntity
import com.example.desarrolloappmoviles.repository.IncidenciaRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.After
import org.junit.Assert.*
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class IncidenciaViewModelTest {
    private lateinit var dao: IncidenciaDaoDePrueba

    @Before fun preparar() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        dao = IncidenciaDaoDePrueba()
    }

    @After fun terminar() { Dispatchers.resetMain() }

    private fun nuevoViewModel() = IncidenciaViewModel(IncidenciaRepository(dao))

    @Test fun formularioVacioMuestraErrores() {
        val vm = nuevoViewModel()
        vm.validarIncidencia()
        assertFalse(vm.mostrarConfirmacion)
        assertTrue(vm.errorCategoria.isNotEmpty())
        assertTrue(vm.errorDescripcion.isNotEmpty())
        assertTrue(vm.errorSector.isNotEmpty())
        assertEquals("", vm.errorPrioridad)
    }

    @Test fun formularioValidoPermiteConfirmarYLimpiar() {
        val vm = formularioCompleto()
        vm.validarIncidencia()
        assertTrue(vm.mostrarConfirmacion)
        assertTrue(dao.registros.value.any { it.descripcion == vm.descripcion })
        vm.limpiarFormulario()
        assertFalse(vm.mostrarConfirmacion)
        assertEquals("", vm.categoria)
        assertEquals("", vm.descripcion)
        assertEquals("", vm.sector)
        assertEquals("Media", vm.prioridad)
    }

    @Test fun descripcionDebeTenerEntreDiezYTrescientosCaracteres() {
        val vm = formularioCompleto()
        listOf("   ", "123456789", "x".repeat(301)).forEach {
            vm.descripcion = it
            vm.mostrarConfirmacion = false
            vm.validarIncidencia()
            assertFalse(vm.mostrarConfirmacion)
            assertTrue(vm.errorDescripcion.isNotEmpty())
        }
        listOf("1234567890", "x".repeat(300)).forEach {
            vm.descripcion = it
            vm.mostrarConfirmacion = false
            vm.validarIncidencia()
            assertTrue(vm.mostrarConfirmacion)
        }
    }

    @Test fun rechazaOpcionesFueraDeLasListas() {
        val vm = formularioCompleto()
        vm.categoria = "Otra"
        vm.sector = "Dirección real"
        vm.prioridad = "Urgente"
        vm.validarIncidencia()
        assertFalse(vm.mostrarConfirmacion)
        assertTrue(vm.errorCategoria.isNotEmpty())
        assertTrue(vm.errorSector.isNotEmpty())
        assertTrue(vm.errorPrioridad.isNotEmpty())
    }

    @Test fun confirmarNoDuplicaElRegistro() {
        val vm = formularioCompleto()
        vm.validarIncidencia()
        vm.validarIncidencia()
        assertEquals(1, dao.registros.value.size)
        assertTrue(dao.registros.value.single().historialAcciones!!.contains("Incidencia registrada"))
    }

    @Test fun falloDeGuardadoPermiteReintentar() {
        val vm = formularioCompleto()
        dao.fallar = true
        vm.validarIncidencia()
        assertFalse(vm.guardando)
        assertFalse(vm.mostrarConfirmacion)
        assertTrue(vm.errorGuardado.isNotEmpty())
        dao.fallar = false
        vm.validarIncidencia()
        assertTrue(vm.mostrarConfirmacion)
        assertEquals(1, dao.registros.value.size)
    }

    @Test fun casoCerradoConservaSuHistorial() {
        val vm = formularioCompleto()
        vm.validarIncidencia()
        val guardada = dao.registros.value.single()
        vm.cerrarIncidencia(guardada)
        val cerrada = dao.registros.value.single()
        assertEquals("Cerrada", cerrada.estado)
        assertTrue(cerrada.historialAcciones!!.contains("Incidencia registrada"))
        assertTrue(cerrada.historialAcciones.contains("Caso cerrado"))
        vm.asignarInspector(cerrada)
        assertEquals(cerrada, dao.registros.value.single())
    }

    @Test fun guardaUbicacionYLimpiaElBorrador() {
        val vm = formularioCompleto()
        vm.latitud = -33.5
        vm.longitud = -70.6
        vm.validarIncidencia()
        val guardada = dao.registros.value.single()
        assertEquals(-33.5, guardada.latitud!!, 0.0001)
        assertEquals(-70.6, guardada.longitud!!, 0.0001)
        vm.limpiarFormulario()
        assertNull(vm.latitud)
        assertNull(vm.longitud)
        assertEquals(-33.5, guardada.latitud!!, 0.0001)
    }

    @Test fun esperaLaLecturaDeUbicacionAntesDeGuardar() {
        val vm = formularioCompleto()
        vm.obteniendoUbicacion = true
        vm.validarIncidencia()
        assertTrue(dao.registros.value.isEmpty())
        vm.obteniendoUbicacion = false
        vm.validarIncidencia()
        assertEquals(1, dao.registros.value.size)
        assertNull(dao.registros.value.single().latitud)
    }

    @Test fun filtraIncidenciasPorEstadoDesdeElRepositorio() {
        val vm = formularioCompleto()
        val ingresada = incidenciaPrueba(1, "Ingresada")
        val cerrada = incidenciaPrueba(2, "Cerrada")
        dao.registros.value = listOf(ingresada, cerrada)

        vm.filtrarIncidenciasPorEstado("Cerrada")

        assertEquals(listOf(cerrada), vm.incidencias.value)
        assertEquals("", vm.errorConsulta)
    }

    @Test fun eliminaLaIncidenciaSeleccionadaYActualizaLaLista() {
        val vm = formularioCompleto()
        val conservar = incidenciaPrueba(1, "Ingresada")
        val eliminar = incidenciaPrueba(2, "Ingresada")
        dao.registros.value = listOf(conservar, eliminar)
        vm.incidencias.value = dao.registros.value

        vm.eliminarIncidencia(eliminar.id)

        assertEquals(listOf(conservar), vm.incidencias.value)
        assertEquals(listOf(conservar), dao.registros.value)
        assertFalse(vm.eliminandoIncidencia)
        assertEquals("", vm.errorConsulta)
    }

    @Test fun cargarDetalleActualizaLaListaYNotificaSiElIdNoExiste() {
        val vm = formularioCompleto()
        val detalle = incidenciaPrueba(4, "Ingresada")
        dao.registros.value = listOf(detalle)

        vm.cargarIncidencia(detalle.id)
        assertTrue(vm.incidencias.value.contains(detalle))
        assertEquals("", vm.errorConsulta)

        vm.cargarIncidencia(999)
        assertEquals("No se pudo cargar el detalle de la incidencia.", vm.errorConsulta)
    }

    @Test fun accionesDeGestionActualizanEstadoYEvitanCambiosRepetidos() {
        val vm = formularioCompleto()
        val original = incidenciaPrueba(5, "Ingresada")
        dao.registros.value = listOf(original)

        vm.asignarInspector(original)
        val asignada = dao.registros.value.single()
        assertEquals("En revisión", asignada.estado)
        assertEquals("Inspector municipal de prueba", asignada.responsable)
        assertTrue(asignada.historialAcciones.orEmpty().contains("Incidencia asignada"))

        vm.asignarInspector(asignada)
        assertEquals(asignada, dao.registros.value.single())
        vm.cerrarIncidencia(asignada)
        assertEquals("Cerrada", dao.registros.value.single().estado)
    }

    @Test fun informaFallosDeCargaFiltroEliminacionYGestion() {
        val vm = formularioCompleto()
        dao.fallarLectura = true
        vm.cargarIncidencias()
        assertEquals("No se pudieron cargar las incidencias. Intenta nuevamente.", vm.errorConsulta)
        vm.filtrarIncidenciasPorEstado("Cerrada")
        assertEquals("No se pudieron filtrar las incidencias.", vm.errorConsulta)
        dao.fallarEliminar = true
        vm.eliminarIncidencia(8)
        assertEquals("No se pudo eliminar la incidencia. Intenta nuevamente.", vm.errorConsulta)

        dao.fallarLectura = false
        val incidencia = incidenciaPrueba(9, "Ingresada")
        dao.registros.value = listOf(incidencia)
        dao.fallarActualizar = true
        vm.asignarInspector(incidencia)
        assertEquals("No se pudo actualizar la incidencia.", vm.errorGestion)
        assertFalse(vm.gestionando)
    }

    private fun incidenciaPrueba(id: Long, estado: String) = IncidenciaEntity(
        id = id,
        categoria = "Iluminación",
        descripcion = "Reporte de prueba de una luminaria",
        prioridad = "Media",
        sector = "Sector A",
        estado = estado,
        correoUsuario = "vecino@ejemplo.com"
    )

    private fun formularioCompleto() = nuevoViewModel().apply {
        categoria = "Iluminación"
        descripcion = "Luminaria apagada en un sector ficticio."
        prioridad = "Media"
        sector = "Sector A"
    }
}

// Sustituye solo la base de datos durante las pruebas; no cambia el código de la app.
private class IncidenciaDaoDePrueba : IncidenciaDao {
    var fallar = false
    var fallarLectura = false
    var fallarActualizar = false
    var fallarEliminar = false
    val registros = MutableStateFlow<List<IncidenciaEntity>>(emptyList())
    override suspend fun obtenerTodas(): List<IncidenciaEntity> {
        check(!fallarLectura) { "Fallo de lectura" }
        return registros.value
    }
    override suspend fun insertar(incidencia: IncidenciaEntity): Long {
        check(!fallar) { "Fallo de prueba" }
        val id = (registros.value.maxOfOrNull { it.id } ?: 0) + 1
        registros.value = registros.value + incidencia.copy(id = id)
        return id
    }
    override suspend fun actualizar(incidencia: IncidenciaEntity) {
        check(!fallarActualizar) { "Fallo al actualizar" }
        registros.value = registros.value.map { if (it.id == incidencia.id) incidencia else it }
    }
    override suspend fun eliminar(id: Long) {
        check(!fallarEliminar) { "Fallo al eliminar" }
        registros.value = registros.value.filterNot { it.id == id }
    }
}
