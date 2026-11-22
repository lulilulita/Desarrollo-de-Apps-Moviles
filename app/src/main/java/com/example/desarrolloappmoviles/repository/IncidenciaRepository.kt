package com.example.desarrolloappmoviles.repository

import com.example.desarrolloappmoviles.model.IncidenciaDao
import com.example.desarrolloappmoviles.model.IncidenciaEntity

class IncidenciaRepository(
    private val incidenciaDao: IncidenciaDao,
    private val xanoApi: XanoApiService? = null
) {
    data class ErroresIncidencia(
        val categoria: String = "", val descripcion: String = "",
        val prioridad: String = "", val sector: String = ""
    ) { val esValido get() = categoria.isEmpty() && descripcion.isEmpty() && prioridad.isEmpty() && sector.isEmpty() }

    private val categoriasValidas = listOf("Iluminación", "Espacio público", "Convivencia")
    private val prioridadesValidas = listOf("Baja", "Media", "Alta")
    private val sectoresValidos = listOf("Sector A", "Sector B", "Sector C")

    fun validarIncidencia(categoria: String, descripcion: String, prioridad: String, sector: String) = ErroresIncidencia(
        categoria = if (categoria !in categoriasValidas) "Selecciona una categoría" else "",
        descripcion = when {
            descripcion.isBlank() -> "Describe la incidencia"
            descripcion.trim().length < 10 -> "Escribe al menos 10 caracteres"
            descripcion.length > 300 -> "El máximo es de 300 caracteres"
            else -> ""
        },
        prioridad = if (prioridad !in prioridadesValidas) "Selecciona una prioridad" else "",
        sector = if (sector !in sectoresValidos) "Selecciona un sector ficticio" else ""
    )

    suspend fun obtenerIncidencias(): List<IncidenciaEntity> = xanoApi?.listar()?.map { it.aEntidad() }
        ?: incidenciaDao.obtenerTodas()

    suspend fun guardar(incidencia: IncidenciaEntity): Long =
        xanoApi?.crear(com.example.desarrolloappmoviles.model.IncidenciaApiModel.desde(incidencia))?.id
            ?: incidenciaDao.insertar(incidencia)

    suspend fun actualizar(incidencia: IncidenciaEntity, reemplazoCompleto: Boolean = false) {
        if (reemplazoCompleto && xanoApi != null) {
            xanoApi.reemplazar(incidencia.id, com.example.desarrolloappmoviles.model.IncidenciaApiModel.desde(incidencia))
            return
        }
        if (xanoApi != null) xanoApi.actualizar(incidencia.id, com.example.desarrolloappmoviles.model.IncidenciaApiModel.desde(incidencia))
        else incidenciaDao.actualizar(incidencia)
    }

    suspend fun obtenerPorId(id: Long) = xanoApi?.obtener(id)?.aEntidad()
        ?: incidenciaDao.obtenerTodas().firstOrNull { it.id == id }
        ?: throw NoSuchElementException("Incidencia no encontrada")
    suspend fun filtrarPorEstado(estado: String) = xanoApi?.filtrar(estado)?.map { it.aEntidad() }
        ?: incidenciaDao.obtenerTodas().filter { it.estado == estado }
    suspend fun reemplazar(incidencia: IncidenciaEntity) = xanoApi?.reemplazar(
        incidencia.id, com.example.desarrolloappmoviles.model.IncidenciaApiModel.desde(incidencia)
    )?.aEntidad() ?: incidencia.also { incidenciaDao.actualizar(it) }
    suspend fun eliminar(id: Long) {
        if (xanoApi != null) {
            val respuesta = xanoApi.eliminar(id)
            check(respuesta.isSuccessful) { "Xano no pudo eliminar la incidencia" }
        } else incidenciaDao.eliminar(id)
    }

}
