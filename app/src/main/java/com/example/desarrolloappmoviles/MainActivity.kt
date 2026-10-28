package com.example.desarrolloappmoviles

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.desarrolloappmoviles.ui.*
import com.example.desarrolloappmoviles.viewmodel.*

private enum class Pantalla { LOGIN, REGISTRO, INICIO, NUEVA_INCIDENCIA, INCIDENCIAS, DETALLE, PERFIL }

class MainActivity : ComponentActivity() {
    // viewModels conserva las instancias al girar el dispositivo y gestiona su ciclo de vida.
    private val incidenciaViewModel: IncidenciaViewModel by viewModels {
        viewModelFactory { initializer {
            IncidenciaViewModel((application as CuidAppApplication).incidenciaRepository)
        } }
    }
    private val loginViewModel: LoginViewModel by viewModels {
        viewModelFactory { initializer {
            LoginViewModel((application as CuidAppApplication).usuarioRepository)
        } }
    }
    private val registroViewModel: RegistroViewModel by viewModels {
        viewModelFactory { initializer {
            RegistroViewModel((application as CuidAppApplication).usuarioRepository)
        } }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var pantalla by rememberSaveable { mutableStateOf(Pantalla.LOGIN) }
            var incidenciaId by rememberSaveable { mutableLongStateOf(0L) }

            fun cerrarSesion() {
                loginViewModel.cerrarSesion()
                incidenciaViewModel.limpiarFormulario()
                incidenciaViewModel.correoUsuarioActual = ""
                incidenciaId = 0
                pantalla = Pantalla.LOGIN
            }

            // Tras terminar el proceso se solicita login; una rotación conserva la sesión.
            val pantallaVisible = if (!loginViewModel.loginExitoso &&
                pantalla != Pantalla.REGISTRO) Pantalla.LOGIN else pantalla

            BackHandler(enabled = pantallaVisible != Pantalla.LOGIN) {
                when (pantallaVisible) {
                    Pantalla.REGISTRO -> pantalla = Pantalla.LOGIN
                    Pantalla.DETALLE -> pantalla = Pantalla.INCIDENCIAS
                    Pantalla.INICIO -> cerrarSesion()
                    else -> pantalla = Pantalla.INICIO
                }
            }

            MaterialTheme {
                Scaffold(Modifier.fillMaxSize()) { padding ->
                    val modifier = Modifier.padding(padding)
                    when (pantallaVisible) {
                        Pantalla.LOGIN -> LoginScreen(
                            viewModel = loginViewModel,
                            abrirInicio = {
                                incidenciaViewModel.correoUsuarioActual = loginViewModel.correo.trim().lowercase()
                                loginViewModel.contrasena = ""
                                pantalla = Pantalla.INICIO
                            },
                            abrirRegistro = { pantalla = Pantalla.REGISTRO },
                            modifier = modifier
                        )
                        Pantalla.REGISTRO -> RegistroScreen(
                            viewModel = registroViewModel,
                            volverLogin = { pantalla = Pantalla.LOGIN },
                            modifier = modifier
                        )
                        Pantalla.INICIO -> InicioScreen(
                            nombreUsuario = loginViewModel.nombreUsuario,
                            abrirNuevaIncidencia = { pantalla = Pantalla.NUEVA_INCIDENCIA },
                            abrirPerfil = { pantalla = Pantalla.PERFIL },
                            verIncidencias = { pantalla = Pantalla.INCIDENCIAS },
                            cerrarSesion = { cerrarSesion() },
                            modifier = modifier
                        )
                        Pantalla.NUEVA_INCIDENCIA -> NuevaIncidenciaScreen(
                            viewModel = incidenciaViewModel,
                            volverInicio = { pantalla = Pantalla.INICIO },
                            modifier = modifier
                        )
                        Pantalla.INCIDENCIAS -> MisIncidenciasScreen(
                            viewModel = incidenciaViewModel,
                            abrirDetalle = {
                                incidenciaId = it.id
                                incidenciaViewModel.errorGestion = ""
                                pantalla = Pantalla.DETALLE
                            },
                            volverInicio = { pantalla = Pantalla.INICIO },
                            modifier = modifier
                        )
                        Pantalla.DETALLE -> DetalleIncidenciaScreen(
                            incidenciaId = incidenciaId,
                            viewModel = incidenciaViewModel,
                            volver = { pantalla = Pantalla.INCIDENCIAS },
                            modifier = modifier
                        )
                        Pantalla.PERFIL -> MiPerfilScreen(
                            nombreUsuario = loginViewModel.nombreUsuario,
                            volverInicio = { pantalla = Pantalla.INICIO },
                            cerrarSesion = { cerrarSesion() },
                            modifier = modifier
                        )
                    }
                }
            }
        }
    }
}
