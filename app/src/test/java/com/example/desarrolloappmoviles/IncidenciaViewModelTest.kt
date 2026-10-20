package com.example.desarrolloappmoviles

import com.example.desarrolloappmoviles.viewmodel.IncidenciaViewModel
import org.junit.Assert.*
import org.junit.Test

class IncidenciaViewModelTest {
    @Test fun formularioVacioMuestraErrores() {
        val vm = IncidenciaViewModel()
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
            vm.validarIncidencia()
            assertFalse(vm.mostrarConfirmacion)
            assertTrue(vm.errorDescripcion.isNotEmpty())
        }
        listOf("1234567890", "x".repeat(300)).forEach {
            vm.descripcion = it
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

    private fun formularioCompleto() = IncidenciaViewModel().apply {
        categoria = "Iluminación"
        descripcion = "Luminaria apagada en un sector ficticio."
        prioridad = "Media"
        sector = "Sector A"
    }
}
