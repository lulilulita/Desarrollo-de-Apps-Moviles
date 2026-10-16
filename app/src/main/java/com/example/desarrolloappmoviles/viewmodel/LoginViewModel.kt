package com.example.desarrolloappmoviles.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class LoginViewModel : ViewModel() {

    var correo by mutableStateOf("")
    var contrasena by mutableStateOf("")

    var errorCorreo by mutableStateOf("")
    var errorContrasena by mutableStateOf("")
    var mensaje by mutableStateOf("")

    fun validacionCorreo(): Boolean {
        if (!correo.trim().matches(Regex("^[\\w.-]+@[\\w.-]+\\.\\w+$"))) {
            return false
        } else {
            return true
        }
    }

    fun validacionContrasena(): Boolean {
        if (contrasena.length > 5) {
            return true
        } else {
            return false
        }
    }

    fun validarLogin() {
        errorCorreo = when {
            correo.isBlank() -> "Ingresa tu correo"
            !validacionCorreo() -> "Ingresa un correo valido"
            else -> ""
        }

        errorContrasena = when {
            contrasena.isBlank() -> "Ingresa tu contrasena"
            !validacionContrasena() ->
                "La contrasena debe tener al menos 6 caracteres"
            else -> ""
        }

        mensaje = if (
            errorCorreo.isEmpty() && errorContrasena.isEmpty()
        ) {
            "Datos validos."
        } else {
            ""
        }
    }
}