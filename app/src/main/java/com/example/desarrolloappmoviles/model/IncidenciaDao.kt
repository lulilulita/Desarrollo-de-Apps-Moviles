package com.example.desarrolloappmoviles.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
@Dao
interface IncidenciaDao {

    @Query("SELECT * FROM incidencias ORDER BY fechaRegistro DESC")
    suspend fun obtenerTodas(): List<IncidenciaEntity>

    @Insert
    suspend fun insertar(incidencia: IncidenciaEntity): Long

    @Update
    suspend fun actualizar(incidencia: IncidenciaEntity)

}
