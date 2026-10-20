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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.desarrolloappmoviles.ui.NuevaIncidenciaScreen
import com.example.desarrolloappmoviles.viewmodel.IncidenciaViewModel
import com.example.desarrolloappmoviles.ui.LoginScreen
import com.example.desarrolloappmoviles.ui.RegistroScreen
import com.example.desarrolloappmoviles.viewmodel.LoginViewModel
import com.example.desarrolloappmoviles.viewmodel.RegistroViewModel

class MainActivity : ComponentActivity() {

    private val incidenciaViewModel: IncidenciaViewModel by viewModels()
    private val loginViewModel: LoginViewModel by viewModels()
    private val registroViewModel: RegistroViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            var mostrarRegistro by rememberSaveable {
                mutableStateOf(false)
            }

            var mostrarIncidencia by rememberSaveable { mutableStateOf(false) }

            BackHandler(enabled = mostrarIncidencia) {
                mostrarIncidencia = false
            }

            // Desde registro, Atras regresa al login.
            BackHandler(enabled = mostrarRegistro) {
                mostrarRegistro = false
            }

            MaterialTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    if (mostrarIncidencia) {
                        NuevaIncidenciaScreen(
                            viewModel = incidenciaViewModel,
                            volverLogin = { mostrarIncidencia = false },
                            modifier = Modifier.padding(innerPadding)
                        )
                    } else if (mostrarRegistro) {
                        RegistroScreen(
                            viewModel = registroViewModel,
                            volverLogin = {
                                mostrarRegistro = false
                            },
                            modifier = Modifier.padding(innerPadding)
                        )
                    } else {
                        LoginScreen(
                            viewModel = loginViewModel,
                            abrirIncidencia = {
                                loginViewModel.contrasena = ""
                                mostrarIncidencia = true
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