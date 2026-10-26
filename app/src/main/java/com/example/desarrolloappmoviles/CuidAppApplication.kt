package com.example.desarrolloappmoviles

import android.app.Application
import androidx.room.Room
import com.example.desarrolloappmoviles.model.CuidAppDatabase
import com.example.desarrolloappmoviles.repository.IncidenciaRepository

class CuidAppApplication : Application() {

    val incidenciaRepository: IncidenciaRepository by lazy {
        val database = Room.databaseBuilder(
            applicationContext,
            CuidAppDatabase::class.java,
            "cuidapp_database"
        ).build()

        IncidenciaRepository(database.incidenciaDao())
    }
}