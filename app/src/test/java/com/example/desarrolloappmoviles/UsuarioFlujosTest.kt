package com.example.desarrolloappmoviles

import com.example.desarrolloappmoviles.model.UsuarioDao
import com.example.desarrolloappmoviles.model.UsuarioEntity
import com.example.desarrolloappmoviles.repository.UsuarioRepository
import com.example.desarrolloappmoviles.viewmodel.LoginViewModel
import com.example.desarrolloappmoviles.viewmodel.RegistroViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class UsuarioFlujosTest {
    private lateinit var dao: UsuarioDaoPrueba
    private lateinit var repository: UsuarioRepository

    @Before fun preparar() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        dao = UsuarioDaoPrueba()
        repository = UsuarioRepository(dao)
    }

    @After fun limpiar() = Dispatchers.resetMain()

    @Test fun loginValidaCamposAntesDeConsultarLaCuenta() {
        val vm = LoginViewModel(repository)
        vm.validarLogin()
        assertTrue(vm.errorCorreo.isNotEmpty())
        assertTrue(vm.errorContrasena.isNotEmpty())
        assertEquals("", vm.mensaje)
        assertFalse(vm.loginExitoso)
    }

    @Test fun loginAceptaCuentaCorrectaYRechazaClaveIncorrecta() {
        runBlocking { repository.registrar("Ana", "Vecina", "ana@example.com", "clave123") }
        val vm = LoginViewModel(repository).apply {
            correo = "ana@example.com"
            contrasena = "clave123"
        }
        vm.validarLogin()
        assertTrue(vm.loginExitoso)
        assertEquals("Ana Vecina", vm.nombreUsuario)
        assertEquals("Inicio de sesión exitoso.", vm.mensaje)

        vm.contrasena = "incorrecta"
        vm.validarLogin()
        assertFalse(vm.loginExitoso)
        assertEquals("Correo o contraseña incorrectos.", vm.mensaje)
    }

    @Test fun loginMuestraErrorSiLaConsultaFalla() {
        runBlocking { repository.registrar("Ana", "Vecina", "ana@example.com", "clave123") }
        dao.fallarLectura = true
        val vm = LoginViewModel(repository).apply {
            correo = "ana@example.com"
            contrasena = "clave123"
        }
        vm.validarLogin()
        assertEquals("No se pudo validar la cuenta.", vm.mensaje)
        assertFalse(vm.loginExitoso)
        assertFalse(vm.cargando)
    }

    @Test fun recuperarSesionManejaErrorDeRoom() {
        dao.fallarSesion = true
        val vm = LoginViewModel(repository)
        assertEquals("No se pudo recuperar la sesión. Inicia sesión nuevamente.", vm.errorSesion)
        assertFalse(vm.restaurandoSesion)
    }

    @Test fun cerrarSesionLimpiaEstadoYManejaError() {
        runBlocking { repository.registrar("Ana", "Vecina", "ana@example.com", "clave123") }
        val vm = LoginViewModel(repository).apply {
            correo = "ana@example.com"
            contrasena = "clave123"
            loginExitoso = true
            nombreUsuario = "Ana Vecina"
        }
        vm.cerrarSesion()
        assertEquals("", vm.correo)
        assertEquals("", vm.contrasena)
        assertFalse(vm.loginExitoso)

        dao.fallarSesion = true
        vm.cerrarSesion()
        assertEquals("No se pudo cerrar la sesión. Intenta nuevamente.", vm.errorSesion)
        assertFalse(vm.cargando)
    }

    @Test fun registroValidaCamposYPermiteCrearCuenta() {
        val vm = RegistroViewModel(repository)
        vm.validarRegistro()
        assertTrue(vm.errorNombre.isNotEmpty())
        assertTrue(vm.errorApellido.isNotEmpty())
        assertTrue(vm.errorCorreo.isNotEmpty())
        assertTrue(vm.errorContrasena.isNotEmpty())
        assertTrue(vm.errorConfirmacion.isNotEmpty())
        assertFalse(vm.registroExitoso)

        vm.nombre = " Ana "
        vm.apellido = "Vecina"
        vm.correo = "ana@example.com"
        vm.contrasena = "clave123"
        vm.confirmarContrasena = "clave123"
        vm.validarRegistro()
        assertTrue(vm.registroExitoso)
        assertEquals("", vm.contrasena)
        assertEquals("", vm.confirmarContrasena)
        assertTrue(vm.mensaje.contains("Cuenta creada"))
    }

    @Test fun registroInformaCorreoDuplicadoYErrorDePersistencia() {
        runBlocking { repository.registrar("Ana", "Vecina", "ana@example.com", "clave123") }
        val vm = RegistroViewModel(repository).apply {
            nombre = "Otra"
            apellido = "Vecina"
            correo = "ana@example.com"
            contrasena = "clave456"
            confirmarContrasena = "clave456"
        }
        vm.validarRegistro()
        assertFalse(vm.registroExitoso)
        assertTrue(vm.mensaje.contains("Ya existe"))

        vm.correo = "otra@example.com"
        dao.fallarInsertar = true
        vm.validarRegistro()
        assertFalse(vm.registroExitoso)
        assertEquals("No se pudo crear la cuenta.", vm.mensaje)
        assertFalse(vm.cargando)
    }
}

private class UsuarioDaoPrueba : UsuarioDao {
    private val usuarios = mutableListOf<UsuarioEntity>()
    var fallarLectura = false
    var fallarSesion = false
    var fallarInsertar = false

    override suspend fun obtenerPorCorreo(correo: String): UsuarioEntity? {
        check(!fallarLectura)
        return usuarios.firstOrNull { it.correo == correo }
    }

    override suspend fun obtenerSesionActiva(): UsuarioEntity? {
        check(!fallarSesion)
        return usuarios.firstOrNull { it.sesionActiva }
    }

    override suspend fun cerrarSesiones() {
        check(!fallarSesion)
        usuarios.indices.forEach { index -> usuarios[index] = usuarios[index].copy(sesionActiva = false) }
    }

    override suspend fun guardarSesion(correo: String) {
        usuarios.indices.forEach { index -> usuarios[index] = usuarios[index].copy(sesionActiva = usuarios[index].correo == correo) }
    }

    override suspend fun insertar(usuario: UsuarioEntity): Long {
        check(!fallarInsertar)
        if (usuarios.any { it.correo == usuario.correo }) return -1L
        usuarios += usuario
        return usuarios.size.toLong()
    }
}
