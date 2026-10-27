package com.example.desarrolloappmoviles.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "incidencias")
data class IncidenciaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val categoria: String,
    val descripcion: String,
    val prioridad: String,
    val sector: String,
    val estado: String = "Ingresada",
    val responsable: String = "Sin asignar",
    val fechaRegistro: Long = System.currentTimeMillis(),
    val latitud: Double? = null,
    val longitud: Double? = null,
    val fotoEvidencia: ByteArray? = null,
    val correoUsuario: String? = null,
    val historialAcciones: String? = null
)

