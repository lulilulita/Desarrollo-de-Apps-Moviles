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

    var restaurandoSesion by mutableStateOf(true)
        private set
    var errorSesion by mutableStateOf("")

    init {
        viewModelScope.launch {
            try {
                val usuario = repository.recuperarSesion()
                if (usuario != null) {
                    correo = usuario.correo
                    nombreUsuario = "${usuario.nombre} ${usuario.apellido}".trim()
                    loginExitoso = true
                }
            } catch (cancelacion: CancellationException) {
                throw cancelacion
            } catch (_: Exception) {
                errorSesion = "No se pudo recuperar la sesión. Inicia sesión nuevamente."
            } finally {
                restaurandoSesion = false
            }
        }
    }

    private fun validacionCorreo(): Boolean =
        correo.trim().matches(Regex("^[\\w.-]+@[\\w.-]+\\.\\w+$"))

    fun cerrarSesion() {
        if (cargando) return
        cargando = true
        viewModelScope.launch {
            try {
                repository.cerrarSesion()
                correo = ""
                contrasena = ""
                errorCorreo = ""
                errorContrasena = ""
                mensaje = ""
                loginExitoso = false
                nombreUsuario = ""
                errorSesion = ""
            } catch (cancelacion: CancellationException) {
                throw cancelacion
            } catch (_: Exception) {
                errorSesion = "No se pudo cerrar la sesión. Intenta nuevamente."
            } finally {
                cargando = false
            }
        }
    }

    fun validarLogin() {
        if (cargando || restaurandoSesion) return
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

                if (usuario != null) repository.guardarSesion(usuario.correo)
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