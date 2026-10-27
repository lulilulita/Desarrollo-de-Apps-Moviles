package com.example.desarrolloappmoviles

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.desarrolloappmoviles.ui.InicioScreen
import com.example.desarrolloappmoviles.ui.LoginScreen
import com.example.desarrolloappmoviles.ui.MisIncidenciasScreen
import com.example.desarrolloappmoviles.ui.NuevaIncidenciaScreen
import com.example.desarrolloappmoviles.ui.RegistroScreen
import com.example.desarrolloappmoviles.viewmodel.IncidenciaViewModel
import com.example.desarrolloappmoviles.viewmodel.LoginViewModel
import com.example.desarrolloappmoviles.viewmodel.RegistroViewModel

class MainActivity : ComponentActivity() {

    private val incidenciaViewModel by lazy {
        IncidenciaViewModel(
            (application as CuidAppApplication).incidenciaRepository
        )
    }

    private val loginViewModel by lazy {
        LoginViewModel(
            (application as CuidAppApplication).usuarioRepository
        )
    }

    private val registroViewModel by lazy {
        RegistroViewModel(
            (application as CuidAppApplication).usuarioRepository
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            var mostrarRegistro by rememberSaveable {
                mutableStateOf(false)
            }
            var mostrarInicio by rememberSaveable {
                mutableStateOf(false)
            }
            var mostrarIncidencia by rememberSaveable {
                mutableStateOf(false)
            }
            var mostrarMisIncidencias by rememberSaveable {
                mutableStateOf(false)
            }

            BackHandler(enabled = mostrarMisIncidencias) {
                mostrarMisIncidencias = false
                mostrarInicio = true
            }

            BackHandler(enabled = mostrarIncidencia) {
                mostrarIncidencia = false
                mostrarInicio = true
            }

            BackHandler(enabled = mostrarInicio) {
                mostrarInicio = false
            }

            BackHandler(enabled = mostrarRegistro) {
                mostrarRegistro = false
            }

            MaterialTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    when {
                        mostrarIncidencia -> {
                            NuevaIncidenciaScreen(
                                viewModel = incidenciaViewModel,
                                volverLogin = {
                                    mostrarIncidencia = false
                                    mostrarInicio = true
                                },
                                modifier = Modifier.padding(innerPadding)
                            )
                        }

                        mostrarMisIncidencias -> {
                            MisIncidenciasScreen(
                                viewModel = incidenciaViewModel,
                                volverInicio = {
                                    mostrarMisIncidencias = false
                                    mostrarInicio = true
                                },
                                modifier = Modifier.padding(innerPadding)
                            )
                        }

                        mostrarInicio -> {
                            InicioScreen(
                                abrirNuevaIncidencia = {
                                    mostrarInicio = false
                                    mostrarIncidencia = true
                                },
                                verIncidencias = {
                                    mostrarInicio = false
                                    mostrarMisIncidencias = true
                                },
                                modifier = Modifier.padding(innerPadding)
                            )
                        }

                        mostrarRegistro -> {
                            RegistroScreen(
                                viewModel = registroViewModel,
                                volverLogin = {
                                    mostrarRegistro = false
                                },
                                modifier = Modifier.padding(innerPadding)
                            )
                        }

                        else -> {
                            LoginScreen(
                                viewModel = loginViewModel,
                                abrirIncidencia = {
                                    loginViewModel.contrasena = ""
                                    mostrarInicio = true
                                },
                                abrirRegistro = {
                                    mostrarRegistro = true
                                },
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                    }
                }
            }
        }
    }
}