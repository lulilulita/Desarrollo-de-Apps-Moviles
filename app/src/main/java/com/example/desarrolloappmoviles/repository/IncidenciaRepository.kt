package com.example.desarrolloappmoviles.repository

import com.example.desarrolloappmoviles.model.IncidenciaDao
import com.example.desarrolloappmoviles.model.IncidenciaEntity
import kotlinx.coroutines.flow.Flow

class IncidenciaRepository(
    private val incidenciaDao: IncidenciaDao
) {
    fun observarIncidencias(): Flow<List<IncidenciaEntity>> =
        incidenciaDao.observarTodas()

    fun observarIncidencia(id: Long): Flow<IncidenciaEntity?> =
        incidenciaDao.observarPorId(id)

    suspend fun guardar(incidencia: IncidenciaEntity): Long =
        incidenciaDao.insertar(incidencia)

    suspend fun actualizar(incidencia: IncidenciaEntity) =
        incidenciaDao.actualizar(incidencia)

    suspend fun eliminar(incidencia: IncidenciaEntity) =
        incidenciaDao.eliminar(incidencia)

    suspend fun agregarIncidenciasDeReferenciaSiFaltan() {
        if (incidenciaDao.contarIncidenciasDeReferencia() > 0) return

        val ahora = System.currentTimeMillis()

        val referencias = listOf(
            IncidenciaEntity(
                categoria = "Iluminación",
                descripcion = "Referencia comunitaria: luminaria apagada.",
                prioridad = "Media",
                sector = "Sector B",
                estado = "Abierta",
                responsable = "Equipo municipal de referencia",
                fechaRegistro = ahora - 86_400_000L
            ),
            IncidenciaEntity(
                categoria = "Espacio público",
                descripcion = "Referencia comunitaria: residuos en una plaza ficticia.",
                prioridad = "Baja",
                sector = "Sector C",
                estado = "En revisión",
                responsable = "Equipo municipal de referencia",
                fechaRegistro = ahora - 172_800_000L
            ),
            IncidenciaEntity(
                categoria = "Convivencia",
                descripcion = "Referencia comunitaria: ruidos reiterados.",
                prioridad = "Media",
                sector = "Sector A",
                estado = "Ingresada",
                responsable = "Equipo municipal de referencia",
                fechaRegistro = ahora
            )
        )

        referencias.forEach { incidenciaDao.insertar(it) }
    }
}