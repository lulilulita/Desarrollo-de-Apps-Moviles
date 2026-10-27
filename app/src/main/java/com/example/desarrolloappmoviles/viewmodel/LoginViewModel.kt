package com.example.desarrolloappmoviles.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.desarrolloappmoviles.repository.UsuarioRepository
import kotlinx.coroutines.launch

class LoginViewModel(
    private val repository: UsuarioRepository
) : ViewModel() {

    var correo by mutableStateOf("")
    var contrasena by mutableStateOf("")

    var errorCorreo by mutableStateOf("")
    var errorContrasena by mutableStateOf("")
    var mensaje by mutableStateOf("")
    var cargando by mutableStateOf(false)
    var loginExitoso by mutableStateOf(false)

    private fun validacionCorreo(): Boolean =
        correo.trim().matches(Regex("^[\\w.-]+@[\\w.-]+\\.\\w+$"))

    fun validarLogin() {
        errorCorreo = when {
            correo.isBlank() -> "Ingresa tu correo"
            !validacionCorreo() -> "Ingresa un correo válido"
            else -> ""
        }

        errorContrasena = when {
            contrasena.isBlank() -> "Ingresa tu contraseña"
            contrasena.length < 6 -> "La contraseña debe tener al menos 6 caracteres"
            else -> ""
        }

        mensaje = ""
        loginExitoso = false

        if (errorCorreo.isNotEmpty() || errorContrasena.isNotEmpty()) return

        cargando = true

        viewModelScope.launch {
            try {
                loginExitoso = repository.validarCredenciales(correo, contrasena)
                mensaje = if (loginExitoso) {
                    "Inicio de sesión exitoso."
                } else {
                    "Correo o contraseña incorrectos."
                }
            } catch (error: Exception) {
                loginExitoso = false
                mensaje = "No se pudo validar la cuenta."
            } finally {
                cargando = false
            }
        }
    }
}