package com.example.desarrolloappmoviles.repository

import com.example.desarrolloappmoviles.model.UsuarioDao
import com.example.desarrolloappmoviles.model.UsuarioEntity
import java.security.MessageDigest

class UsuarioRepository(private val dao: UsuarioDao) {

    data class ErroresRegistro(
        val nombre: String = "", val apellido: String = "", val correo: String = "",
        val contrasena: String = "", val confirmacion: String = ""
    ) { val esValido get() = nombre.isEmpty() && apellido.isEmpty() && correo.isEmpty() && contrasena.isEmpty() && confirmacion.isEmpty() }

    data class ErroresLogin(val correo: String = "", val contrasena: String = "") {
        val esValido get() = correo.isEmpty() && contrasena.isEmpty()
    }

    fun validarRegistro(nombre: String, apellido: String, correo: String, contrasena: String, confirmacion: String) = ErroresRegistro(
        nombre = if (nombre.isBlank()) "Ingresa tu nombre" else "",
        apellido = if (apellido.isBlank()) "Ingresa tu apellido" else "",
        correo = validarCorreo(correo),
        contrasena = validarContrasena(contrasena),
        confirmacion = when { confirmacion.isBlank() -> "Confirma tu contraseña"; confirmacion != contrasena -> "Las contraseñas no coinciden"; else -> "" }
    )

    fun validarLogin(correo: String, contrasena: String) = ErroresLogin(
        correo = validarCorreo(correo), contrasena = validarContrasena(contrasena)
    )

    suspend fun registrar(nombre: String, apellido: String, correo: String, contrasena: String): Boolean {
        val usuario = UsuarioEntity(
            nombre = nombre.trim(),
            apellido = apellido.trim(),
            correo = normalizarCorreo(correo),
            contrasenaHash = hashContrasena(contrasena)
        )
        return dao.insertar(usuario) != -1L
    }

    suspend fun obtenerUsuarioValidado(
        correo: String,
        contrasena: String
    ): UsuarioEntity? {
        val usuario = dao.obtenerPorCorreo(normalizarCorreo(correo))
            ?: return null

        return if (usuario.contrasenaHash == hashContrasena(contrasena)) {
            usuario
        } else {
            null
        }
    }

    suspend fun guardarSesion(correo: String) = dao.guardarSesion(normalizarCorreo(correo))

    suspend fun recuperarSesion(): UsuarioEntity? = dao.obtenerSesionActiva()

    suspend fun cerrarSesion() = dao.cerrarSesiones()

    private fun normalizarCorreo(correo: String): String =
        correo.trim().lowercase()

    private fun validarCorreo(correo: String): String = when {
        correo.isBlank() -> "Ingresa tu correo"
        !correo.trim().matches(Regex("^[\\w.-]+@[\\w.-]+\\.\\w+$")) -> "Ingresa un correo válido"
        else -> ""
    }

    private fun validarContrasena(contrasena: String): String = when {
        contrasena.isBlank() -> "Ingresa tu contraseña"
        contrasena.length < 6 -> "La contraseña debe tener al menos 6 caracteres"
        else -> ""
    }

    // Se conserva el hash existente para que las cuentas guardadas sigan funcionando.
    private fun hashContrasena(contrasena: String): String =
        MessageDigest.getInstance("SHA-256")
            .digest(contrasena.toByteArray(Charsets.UTF_8))
            .joinToString("") { byte -> "%02x".format(byte) }
}
