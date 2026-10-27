package com.example.desarrolloappmoviles.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.desarrolloappmoviles.repository.UsuarioRepository
import kotlinx.coroutines.launch

class RegistroViewModel(
    private val repository: UsuarioRepository
) : ViewModel() {

    var correo by mutableStateOf("")
    var contrasena by mutableStateOf("")
    var confirmarContrasena by mutableStateOf("")

    var errorCorreo by mutableStateOf("")
    var errorContrasena by mutableStateOf("")
    var errorConfirmacion by mutableStateOf("")
    var mensaje by mutableStateOf("")
    var registroExitoso by mutableStateOf(false)

    fun validacionCorreo(): Boolean =
        correo.trim().matches(Regex("^[\\w.-]+@[\\w.-]+\\.\\w+$"))

    fun validarRegistro() {
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

        errorConfirmacion = when {
            confirmarContrasena.isBlank() -> "Confirma tu contraseña"
            confirmarContrasena != contrasena -> "Las contraseñas no coinciden"
            else -> ""
        }

        mensaje = ""
        registroExitoso = false

        if (
            errorCorreo.isEmpty() &&
            errorContrasena.isEmpty() &&
            errorConfirmacion.isEmpty()
        ) {
            viewModelScope.launch {
                try {
                    val registrado = repository.registrar(correo, contrasena)

                    if (registrado) {
                        registroExitoso = true
                        mensaje = "Cuenta creada correctamente. Ahora puedes iniciar sesión."
                    } else {
                        mensaje = "Ya existe una cuenta con este correo. Inicia sesión."
                    }
                } catch (error: Exception) {
                    mensaje = "No se pudo crear la cuenta."
                }
            }
        }
    }
}