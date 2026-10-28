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

    private fun hashContrasena(contrasena: String): String =
        MessageDigest.getInstance("SHA-256")
            .digest(contrasena.toByteArray(Charsets.UTF_8))
            .joinToString("") { byte ->
                (byte.toInt() and 0xff).toString(16).padStart(2, '0')
            }
}