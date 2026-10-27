package com.example.desarrolloappmoviles.repository

import com.example.desarrolloappmoviles.model.UsuarioDao
import com.example.desarrolloappmoviles.model.UsuarioEntity
import java.security.MessageDigest

class UsuarioRepository(private val dao: UsuarioDao) {

    suspend fun registrar(correo: String, contrasena: String): Boolean {
        val usuario = UsuarioEntity(
            correo = normalizarCorreo(correo),
            contrasenaHash = hashContrasena(contrasena)
        )
        return dao.insertar(usuario) != -1L
    }

    suspend fun validarCredenciales(
        correo: String,
        contrasena: String
    ): Boolean {
        val usuario = dao.obtenerPorCorreo(normalizarCorreo(correo))
            ?: return false

        return usuario.contrasenaHash == hashContrasena(contrasena)
    }

    private fun normalizarCorreo(correo: String): String =
        correo.trim().lowercase()

    private fun hashContrasena(contrasena: String): String =
        MessageDigest.getInstance("SHA-256")
            .digest(contrasena.toByteArray(Charsets.UTF_8))
            .joinToString("") { byte ->
                (byte.toInt() and 0xff).toString(16).padStart(2, '0')
            }
}