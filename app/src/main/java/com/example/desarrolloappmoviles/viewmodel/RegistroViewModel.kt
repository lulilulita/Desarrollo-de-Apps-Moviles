package com.example.desarrolloappmoviles.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class RegistroViewModel : ViewModel() {

    var correo by mutableStateOf("")
    var contrasena by mutableStateOf("")
    var confirmarContrasena by mutableStateOf("")

    var errorCorreo by mutableStateOf("")
    var errorContrasena by mutableStateOf("")
    var errorConfirmacion by mutableStateOf("")
    var mensaje by mutableStateOf("")

    fun validacionCorreo(): Boolean {
        if (!correo.trim().matches(Regex("^[\\w.-]+@[\\w.-]+\\.\\w+$"))) {
            return false
        } else {
            return true
        }
    }

    fun validarRegistro() {
        errorCorreo = when {
            correo.isBlank() -> "Ingresa tu correo"
            !validacionCorreo() -> "Ingresa un correo valido"
            else -> ""
        }

        errorContrasena = when {
            contrasena.isBlank() -> "Ingresa tu contrasena"
            contrasena.length < 6 ->
                "La contrasena debe tener al menos 6 caracteres"
            else -> ""
        }

        errorConfirmacion = when {
            confirmarContrasena.isBlank() -> "Confirma tu contrasena"
            confirmarContrasena != contrasena ->
                "Las contrasenas no coinciden"
            else -> ""
        }

        mensaje = if (
            errorCorreo.isEmpty() &&
            errorContrasena.isEmpty() &&
            errorConfirmacion.isEmpty()
        ) {
            "Datos validos. Guardado pendiente."
        } else {
            ""
        }
    }
}