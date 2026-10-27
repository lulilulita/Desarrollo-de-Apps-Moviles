package com.example.desarrolloappmoviles

import android.app.Application
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.desarrolloappmoviles.model.CuidAppDatabase
import com.example.desarrolloappmoviles.repository.IncidenciaRepository
import com.example.desarrolloappmoviles.repository.UsuarioRepository

class CuidAppApplication : Application() {

    private val database: CuidAppDatabase by lazy {
        Room.databaseBuilder(
            applicationContext,
            CuidAppDatabase::class.java,
            "cuidapp_database"
        )
            .addMigrations(MIGRATION_1_2)
            .build()
    }

    val incidenciaRepository: IncidenciaRepository by lazy {
        IncidenciaRepository(database.incidenciaDao())
    }

    val usuarioRepository: UsuarioRepository by lazy {
        UsuarioRepository(database.usuarioDao())
    }

    companion object {
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `usuarios` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `correo` TEXT NOT NULL,
                        `contrasenaHash` TEXT NOT NULL
                    )
                    """.trimIndent()
                )
                database.execSQL(
                    "CREATE UNIQUE INDEX IF NOT EXISTS `index_usuarios_correo` " +
                            "ON `usuarios` (`correo`)"
                )
            }
        }
    }
}