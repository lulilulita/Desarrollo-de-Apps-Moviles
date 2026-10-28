package com.example.desarrolloappmoviles.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.desarrolloappmoviles.repository.UsuarioRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.CancellationException

class RegistroViewModel(
    private val repository: UsuarioRepository
) : ViewModel() {

    var nombre by mutableStateOf("")
    var apellido by mutableStateOf("")
    var correo by mutableStateOf("")
    var contrasena by mutableStateOf("")
    var confirmarContrasena by mutableStateOf("")

    var errorNombre by mutableStateOf("")
    var errorApellido by mutableStateOf("")
    var errorCorreo by mutableStateOf("")
    var errorContrasena by mutableStateOf("")
    var errorConfirmacion by mutableStateOf("")
    var mensaje by mutableStateOf("")
    var registroExitoso by mutableStateOf(false)
    var cargando by mutableStateOf(false)
        private set

    private fun validacionCorreo(): Boolean =
        correo.trim().matches(Regex("^[\\w.-]+@[\\w.-]+\\.\\w+$"))

    fun validarRegistro() {
        if (cargando) return
        errorNombre = if (nombre.isBlank()) "Ingresa tu nombre" else ""
        errorApellido = if (apellido.isBlank()) "Ingresa tu apellido" else ""

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

        errorConfirmacion = when {
            confirmarContrasena.isBlank() -> "Confirma tu contraseña"
            confirmarContrasena != contrasena ->
                "Las contraseñas no coinciden"
            else -> ""
        }

        mensaje = ""
        registroExitoso = false

        if (
            errorNombre.isEmpty() &&
            errorApellido.isEmpty() &&
            errorCorreo.isEmpty() &&
            errorContrasena.isEmpty() &&
            errorConfirmacion.isEmpty()
        ) {
            val nombreActual = nombre
            val apellidoActual = apellido
            val correoActual = correo
            val contrasenaActual = contrasena
            cargando = true
            viewModelScope.launch {
                try {
                    registroExitoso = repository.registrar(
                        nombre = nombreActual,
                        apellido = apellidoActual,
                        correo = correoActual,
                        contrasena = contrasenaActual
                    )

                    if (registroExitoso) {
                        contrasena = ""
                        confirmarContrasena = ""
                    }
                    mensaje = if (registroExitoso) {
                        "Cuenta creada correctamente. Ahora puedes iniciar sesión."
                    } else {
                        "Ya existe una cuenta con este correo. Inicia sesión."
                    }
                } catch (cancelacion: CancellationException) {
                    throw cancelacion
                } catch (_: Exception) {
                    registroExitoso = false
                    mensaje = "No se pudo crear la cuenta."
                } finally {
                    cargando = false
                }
            }
        }
    }
}