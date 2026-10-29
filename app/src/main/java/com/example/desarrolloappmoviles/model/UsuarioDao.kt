package com.example.desarrolloappmoviles.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UsuarioDao {

    @Query("SELECT * FROM usuarios WHERE correo = :correo LIMIT 1")
    suspend fun obtenerPorCorreo(correo: String): UsuarioEntity?

    @Query("SELECT * FROM usuarios WHERE sesionActiva = 1 LIMIT 1")
    suspend fun obtenerSesionActiva(): UsuarioEntity?

    @Query("UPDATE usuarios SET sesionActiva = 0")
    suspend fun cerrarSesiones()

    // La comparación vale 1 para esa cuenta y 0 para las demás: queda una sola activa.
    @Query("UPDATE usuarios SET sesionActiva = (correo = :correo)")
    suspend fun guardarSesion(correo: String)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertar(usuario: UsuarioEntity): Long
}