package com.example.desarrolloappmoviles.repository

import com.example.desarrolloappmoviles.model.UsuarioDao
import com.example.desarrolloappmoviles.model.UsuarioEntity
import java.security.MessageDigest

class UsuarioRepository(private val dao: UsuarioDao) {

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

    private fun normalizarCorreo(correo: String): String =
        correo.trim().lowercase()

    // Se conserva el hash existente para que las cuentas guardadas sigan funcionando.
    private fun hashContrasena(contrasena: String): String =
        MessageDigest.getInstance("SHA-256")
            .digest(contrasena.toByteArray(Charsets.UTF_8))
            .joinToString("") { byte -> "%02x".format(byte) }
}