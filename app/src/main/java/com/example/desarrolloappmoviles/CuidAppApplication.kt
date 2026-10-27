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
            .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5)
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
        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "ALTER TABLE `usuarios` ADD COLUMN `nombre` TEXT NOT NULL DEFAULT ''"
                )
                database.execSQL(
                    "ALTER TABLE `usuarios` ADD COLUMN `apellido` TEXT NOT NULL DEFAULT ''"
                )
            }
        }
        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "ALTER TABLE `incidencias` ADD COLUMN `fotoEvidencia` BLOB"
                )
            }
        }
        private val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "ALTER TABLE `incidencias` ADD COLUMN `correoUsuario` TEXT"
                )
            }
        }
    }
}