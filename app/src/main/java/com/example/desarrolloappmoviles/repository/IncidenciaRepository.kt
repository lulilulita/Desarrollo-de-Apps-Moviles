package com.example.desarrolloappmoviles.repository

import com.example.desarrolloappmoviles.model.IncidenciaDao
import com.example.desarrolloappmoviles.model.IncidenciaEntity

class IncidenciaRepository(
    private val incidenciaDao: IncidenciaDao
) {
    suspend fun obtenerIncidencias(): List<IncidenciaEntity> =
        incidenciaDao.obtenerTodas()

    suspend fun guardar(incidencia: IncidenciaEntity): Long =
        incidenciaDao.insertar(incidencia)

    suspend fun actualizar(incidencia: IncidenciaEntity) =
        incidenciaDao.actualizar(incidencia)

}
