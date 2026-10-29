package com.example.desarrolloappmoviles

import com.example.desarrolloappmoviles.model.UsuarioDao
import com.example.desarrolloappmoviles.model.UsuarioEntity
import com.example.desarrolloappmoviles.repository.UsuarioRepository
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.test.resetMain
import com.example.desarrolloappmoviles.viewmodel.LoginViewModel
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
    @Test fun otraInstanciaRecuperaLaSesionYElLogoutLaQuita() = runBlocking {
        val dao = UsuarioDaoDePrueba()
        val primero = UsuarioRepository(dao)
        primero.registrar("Vecino", "Ficticio", "vecino@ejemplo.com", "clave123")
        primero.guardarSesion("vecino@ejemplo.com")
        val segundo = UsuarioRepository(dao)
        assertEquals("vecino@ejemplo.com", segundo.recuperarSesion()?.correo)
        segundo.cerrarSesion()
        assertNull(primero.recuperarSesion())
        assertNotNull(primero.obtenerUsuarioValidado("vecino@ejemplo.com", "clave123"))
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test fun nuevoViewModelRecuperaLaCuentaHastaCerrarSesion() = runBlocking {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        try {
            val repo = UsuarioRepository(UsuarioDaoDePrueba())
            repo.registrar("Vecino", "Ficticio", "vecino@ejemplo.com", "clave123")
            repo.guardarSesion("vecino@ejemplo.com")
            val login = LoginViewModel(repo)
            assertTrue(login.loginExitoso)
            assertFalse(login.restaurandoSesion)
            assertEquals("Vecino Ficticio", login.nombreUsuario)
            assertEquals("", login.contrasena)
            login.cerrarSesion()
            val siguienteInicio = LoginViewModel(repo)
            assertFalse(siguienteInicio.loginExitoso)
            assertFalse(siguienteInicio.restaurandoSesion)
        } finally {
            Dispatchers.resetMain()
        }
    }

}

private class UsuarioDaoDePrueba : UsuarioDao {
    var usuario: UsuarioEntity? = null
    override suspend fun obtenerPorCorreo(correo: String) = usuario?.takeIf { it.correo == correo }
    override suspend fun obtenerSesionActiva() = usuario?.takeIf { it.sesionActiva }
    override suspend fun cerrarSesiones() { usuario = usuario?.copy(sesionActiva = false) }
    override suspend fun guardarSesion(correo: String) {
        usuario = usuario?.copy(sesionActiva = usuario?.correo == correo)
    }
    override suspend fun insertar(usuario: UsuarioEntity): Long {
        if (this.usuario?.correo == usuario.correo) return -1L
        this.usuario = usuario
        return 1L
    }
}
