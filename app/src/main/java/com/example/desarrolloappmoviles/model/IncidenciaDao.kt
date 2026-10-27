package com.example.desarrolloappmoviles.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import androidx.room.Delete
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

    @Delete
    suspend fun eliminar(incidencia: IncidenciaEntity)

    @Query(
        "SELECT COUNT(*) FROM incidencias " +
                "WHERE responsable = 'Equipo municipal de referencia'"
    )
    suspend fun contarIncidenciasDeReferencia(): Int
}