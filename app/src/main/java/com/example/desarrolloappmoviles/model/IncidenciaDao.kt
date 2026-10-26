package com.example.desarrolloappmoviles.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface IncidenciaDao {

    @Query("SELECT * FROM incidencias ORDER BY fechaRegistro DESC")
    fun observarTodas(): Flow<List<IncidenciaEntity>>

    @Query("SELECT * FROM incidencias WHERE id = :id LIMIT 1")
    fun observarPorId(id: Long): Flow<IncidenciaEntity?>

    @Insert
    suspend fun insertar(incidencia: IncidenciaEntity): Long

    @Update
    suspend fun actualizar(incidencia: IncidenciaEntity)
}