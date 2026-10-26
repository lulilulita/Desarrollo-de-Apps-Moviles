package com.example.desarrolloappmoviles.model

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [IncidenciaEntity::class],
    version = 1,
    exportSchema = false
)
abstract class CuidAppDatabase : RoomDatabase() {
    abstract fun incidenciaDao(): IncidenciaDao
}