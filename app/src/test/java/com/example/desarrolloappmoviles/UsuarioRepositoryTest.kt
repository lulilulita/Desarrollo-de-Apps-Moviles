package com.example.desarrolloappmoviles

import com.example.desarrolloappmoviles.model.UsuarioDao
import com.example.desarrolloappmoviles.model.UsuarioEntity
import com.example.desarrolloappmoviles.repository.UsuarioRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

class UsuarioRepositoryTest {
    @Test fun conservaLaCompatibilidadConUnaCuentaAnterior() = runBlocking {
        val dao = UsuarioDaoDePrueba()
        dao.usuario = UsuarioEntity(correo = "vecino@ejemplo.com", contrasenaHash = "5ac0852e770506dcd80f1a36d20ba7878bf82244b836d9324593bd14bc56dcb5")
        val repositorio = UsuarioRepository(dao)
        assertNotNull(repositorio.obtenerUsuarioValidado(" VECINO@ejemplo.com ", "clave123"))
        assertNull(repositorio.obtenerUsuarioValidado("vecino@ejemplo.com", "otra123"))
    }

    @Test fun registraYRechazaCorreoDuplicado() = runBlocking {
        val repositorio = UsuarioRepository(UsuarioDaoDePrueba())
        assertTrue(repositorio.registrar("Vecino", "Ficticio", "vecino@ejemplo.com", "clave123"))
        assertFalse(repositorio.registrar("Otro", "Ficticio", "VECINO@ejemplo.com", "otra123"))
        assertNotNull(repositorio.obtenerUsuarioValidado("vecino@ejemplo.com", "clave123"))
    }
}

private class UsuarioDaoDePrueba : UsuarioDao {
    var usuario: UsuarioEntity? = null
    override suspend fun obtenerPorCorreo(correo: String) = usuario?.takeIf { it.correo == correo }
    override suspend fun insertar(usuario: UsuarioEntity): Long {
        if (this.usuario?.correo == usuario.correo) return -1L
        this.usuario = usuario
        return 1L
    }
}
