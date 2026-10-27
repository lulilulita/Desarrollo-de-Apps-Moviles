package com.example.desarrolloappmoviles.model

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        IncidenciaEntity::class,
        UsuarioEntity::class
    ],
    version = 6,
    exportSchema = false
)
abstract class CuidAppDatabase : RoomDatabase() {

    abstract fun incidenciaDao(): IncidenciaDao

    abstract fun usuarioDao(): UsuarioDao
}