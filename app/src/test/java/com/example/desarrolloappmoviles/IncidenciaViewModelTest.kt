package com.example.desarrolloappmoviles

import com.example.desarrolloappmoviles.viewmodel.IncidenciaViewModel
import com.example.desarrolloappmoviles.model.IncidenciaDao
import com.example.desarrolloappmoviles.model.IncidenciaEntity
import com.example.desarrolloappmoviles.repository.IncidenciaRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
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
        vm.registrarAccionGestion(guardada, "Caso cerrado", nuevoEstado = "Cerrada")
        val cerrada = dao.registros.value.single()
        assertEquals("Cerrada", cerrada.estado)
        assertTrue(cerrada.historialAcciones!!.contains("Incidencia registrada"))
        assertTrue(cerrada.historialAcciones.contains("Caso cerrado"))
        vm.registrarAccionGestion(cerrada, "Reabrir", nuevoEstado = "En revisión")
        assertEquals(cerrada, dao.registros.value.single())
    }

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
    val registros = MutableStateFlow<List<IncidenciaEntity>>(emptyList())
    override fun observarTodas() = registros
    override fun observarPorId(id: Long) = registros.map { lista -> lista.find { it.id == id } }
    override suspend fun insertar(incidencia: IncidenciaEntity): Long {
        check(!fallar) { "Fallo de prueba" }
        val id = (registros.value.maxOfOrNull { it.id } ?: 0) + 1
        registros.value = registros.value + incidencia.copy(id = id)
        return id
    }
    override suspend fun actualizar(incidencia: IncidenciaEntity) {
        registros.value = registros.value.map { if (it.id == incidencia.id) incidencia else it }
    }
}
