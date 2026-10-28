package com.example.desarrolloappmoviles

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.desarrolloappmoviles.ui.*
import com.example.desarrolloappmoviles.viewmodel.*

class MainActivity : ComponentActivity() {
    // Una fábrica entrega el repositorio a cada ViewModel. viewModels los conserva al rotar.
    private val fabricaViewModels by lazy {
        val app = application as CuidAppApplication
        viewModelFactory {
            initializer { LoginViewModel(app.usuarioRepository) }
            initializer { RegistroViewModel(app.usuarioRepository) }
            initializer { IncidenciaViewModel(app.incidenciaRepository) }
        }
    }
    private val loginViewModel: LoginViewModel by viewModels { fabricaViewModels }
    private val registroViewModel: RegistroViewModel by viewModels { fabricaViewModels }
    private val incidenciaViewModel: IncidenciaViewModel by viewModels { fabricaViewModels }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Scaffold(Modifier.fillMaxSize()) { padding ->
                    Navegacion(
                        loginViewModel = loginViewModel,
                        registroViewModel = registroViewModel,
                        incidenciaViewModel = incidenciaViewModel,
                        modifier = Modifier.padding(padding)
                    )
                }
            }
        }
    }
}
