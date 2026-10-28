package com.example.desarrolloappmoviles.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.desarrolloappmoviles.repository.UsuarioRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.CancellationException

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
    var nombreUsuario by mutableStateOf("")

    private fun validacionCorreo(): Boolean =
        correo.trim().matches(Regex("^[\\w.-]+@[\\w.-]+\\.\\w+$"))

    fun cerrarSesion() {
        correo = ""
        contrasena = ""
        errorCorreo = ""
        errorContrasena = ""
        mensaje = ""
        loginExitoso = false
        nombreUsuario = ""
    }

    fun validarLogin() {
        if (cargando) return
        errorCorreo = when {
            correo.isBlank() -> "Ingresa tu correo"
            !validacionCorreo() -> "Ingresa un correo válido"
            else -> ""
        }

        errorContrasena = when {
            contrasena.isBlank() -> "Ingresa tu contraseña"
            contrasena.length < 6 ->
                "La contraseña debe tener al menos 6 caracteres"
            else -> ""
        }

        mensaje = ""
        loginExitoso = false
        nombreUsuario = ""

        if (errorCorreo.isNotEmpty() || errorContrasena.isNotEmpty()) {
            return
        }

        val correoActual = correo
        val contrasenaActual = contrasena
        cargando = true

        viewModelScope.launch {
            try {
                val usuario = repository.obtenerUsuarioValidado(
                    correo = correoActual,
                    contrasena = contrasenaActual
                )

                loginExitoso = usuario != null
                nombreUsuario = usuario?.let {
                    "${it.nombre} ${it.apellido}".trim()
                }.orEmpty()

                mensaje = if (loginExitoso) {
                    "Inicio de sesión exitoso."
                } else {
                    "Correo o contraseña incorrectos."
                }
            } catch (cancelacion: CancellationException) {
                throw cancelacion
            } catch (_: Exception) {
                loginExitoso = false
                nombreUsuario = ""
                mensaje = "No se pudo validar la cuenta."
            } finally {
                cargando = false
            }
        }
    }
}