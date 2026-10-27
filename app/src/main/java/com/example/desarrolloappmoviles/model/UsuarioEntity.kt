package com.example.desarrolloappmoviles.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "usuarios",
    indices = [Index(value = ["correo"], unique = true)]
)
data class UsuarioEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(defaultValue = "''")
    val nombre: String = "",

    @ColumnInfo(defaultValue = "''")
    val apellido: String = "",

    val correo: String,
    val contrasenaHash: String
)