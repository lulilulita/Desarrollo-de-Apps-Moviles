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
}