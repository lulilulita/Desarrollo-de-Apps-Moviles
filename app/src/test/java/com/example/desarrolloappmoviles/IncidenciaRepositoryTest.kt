package com.example.desarrolloappmoviles

import com.example.desarrolloappmoviles.model.IncidenciaApiModel
import com.example.desarrolloappmoviles.model.IncidenciaDao
import com.example.desarrolloappmoviles.model.IncidenciaEntity
import com.example.desarrolloappmoviles.repository.IncidenciaRepository
import com.example.desarrolloappmoviles.repository.XanoApiService
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert.*
import org.junit.Test
import retrofit2.Response

class IncidenciaRepositoryTest {
    @Test fun usaRutasXanoParaListarCrearConsultarFiltrarActualizarYEliminar() = runBlocking {
        val api = XanoPrueba()
        val repo = IncidenciaRepository(IncidenciaDaoPrueba(), api)
        assertTrue(repo.obtenerIncidencias().isEmpty())

        val id = repo.guardar(incidencia(0, "Ingresada"))
        assertEquals(1L, id)
        assertEquals(1, repo.obtenerIncidencias().size)
        assertEquals(id, repo.obtenerPorId(id).id)
        assertEquals(1, repo.filtrarPorEstado("Ingresada").size)

        repo.actualizar(incidencia(id, "En revisión"))
        assertEquals("En revisión", repo.obtenerPorId(id).estado)
        repo.actualizar(incidencia(id, "Cerrada"), reemplazoCompleto = true)
        assertEquals("Cerrada", repo.obtenerPorId(id).estado)
        assertEquals("Cerrada", repo.reemplazar(incidencia(id, "Cerrada")).estado)
        repo.eliminar(id)
        assertTrue(repo.obtenerIncidencias().isEmpty())

        assertEquals(1, api.actualizacionesParciales)
        assertEquals(2, api.reemplazos)
        assertEquals(1, api.eliminaciones)
    }

    @Test fun usaRoomCuandoNoHayApiYFallaSiElIdNoExiste() = runBlocking {
        val dao = IncidenciaDaoPrueba()
        val repo = IncidenciaRepository(dao)
        val id = repo.guardar(incidencia(0, "Ingresada"))
        assertEquals(id, repo.obtenerPorId(id).id)
        assertEquals(1, repo.filtrarPorEstado("Ingresada").size)
        repo.actualizar(incidencia(id, "En revisión"))
        repo.reemplazar(incidencia(id, "Cerrada"))
        assertEquals("Cerrada", repo.obtenerPorId(id).estado)
        repo.eliminar(id)
        assertTrue(repo.obtenerIncidencias().isEmpty())

        try {
            repo.obtenerPorId(99)
            fail("Debía informar que no existe")
        } catch (esperado: NoSuchElementException) {
            assertEquals("Incidencia no encontrada", esperado.message)
        }
    }

    private fun incidencia(id: Long, estado: String) = IncidenciaEntity(
        id = id,
        categoria = "Iluminación",
        descripcion = "Luminaria apagada en un pasaje ficticio",
        prioridad = "Media",
        sector = "Sector A",
        estado = estado,
        correoUsuario = "vecino@example.com",
        latitud = -33.4,
        longitud = -70.6
    )
}

private class XanoPrueba : XanoApiService {
    private val registros = mutableListOf<IncidenciaApiModel>()
    var actualizacionesParciales = 0
    var reemplazos = 0
    var eliminaciones = 0

    override suspend fun listar() = registros.toList()
    override suspend fun obtener(id: Long) = registros.first { it.id == id }
    override suspend fun filtrar(estado: String) = registros.filter { it.estado == estado }
    override suspend fun crear(incidencia: IncidenciaApiModel): IncidenciaApiModel {
        val nuevo = incidencia.copy(id = (registros.maxOfOrNull { it.id } ?: 0) + 1)
        registros += nuevo
        return nuevo
    }
    override suspend fun reemplazar(id: Long, incidencia: IncidenciaApiModel): IncidenciaApiModel {
        reemplazos++
        val nuevo = incidencia.copy(id = id)
        reemplazarRegistro(id, nuevo)
        return nuevo
    }
    override suspend fun actualizar(id: Long, incidencia: IncidenciaApiModel): IncidenciaApiModel {
        actualizacionesParciales++
        val nuevo = incidencia.copy(id = id)
        reemplazarRegistro(id, nuevo)
        return nuevo
    }
    override suspend fun eliminar(id: Long): Response<Unit> {
        eliminaciones++
        registros.removeAll { it.id == id }
        return Response.success(Unit)
    }

    private fun reemplazarRegistro(id: Long, nuevo: IncidenciaApiModel) {
        val indice = registros.indexOfFirst { it.id == id }
        if (indice >= 0) registros[indice] = nuevo
    }
}

private class IncidenciaDaoPrueba : IncidenciaDao {
    val registros = MutableStateFlow<List<IncidenciaEntity>>(emptyList())
    override suspend fun obtenerTodas() = registros.value
    override suspend fun insertar(incidencia: IncidenciaEntity): Long {
        val id = (registros.value.maxOfOrNull { it.id } ?: 0) + 1
        registros.value = registros.value + incidencia.copy(id = id)
        return id
    }
    override suspend fun actualizar(incidencia: IncidenciaEntity) {
        registros.value = registros.value.map { if (it.id == incidencia.id) incidencia else it }
    }
    override suspend fun eliminar(id: Long) {
        registros.value = registros.value.filterNot { it.id == id }
    }
}
