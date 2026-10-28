package com.example.desarrolloappmoviles.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.desarrolloappmoviles.viewmodel.IncidenciaViewModel
import com.example.desarrolloappmoviles.viewmodel.LoginViewModel
import com.example.desarrolloappmoviles.viewmodel.RegistroViewModel

// Mismo patrón de Navegacion.kt del profesor, con las pantallas de CuidApp.
@Composable
fun Navegacion(
    loginViewModel: LoginViewModel,
    registroViewModel: RegistroViewModel,
    incidenciaViewModel: IncidenciaViewModel,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val entrada by navController.currentBackStackEntryAsState()
    val rutaActual = entrada?.destination?.route

    fun cerrarSesion() {
        loginViewModel.cerrarSesion()
        incidenciaViewModel.limpiarFormulario()
        incidenciaViewModel.correoUsuarioActual = ""
        navController.navigate("login") {
            popUpTo(navController.graph.id) { inclusive = true }
            launchSingleTop = true
        }
    }

    // Si Android termina el proceso, se pide login de nuevo. Rotar conserva el ViewModel.
    LaunchedEffect(rutaActual, loginViewModel.loginExitoso) {
        if (!loginViewModel.loginExitoso && rutaActual != null &&
            rutaActual != "login" && rutaActual != "registro") {
            cerrarSesion()
        }
    }

    NavHost(navController = navController, startDestination = "login", modifier = modifier) {
        composable("login") {
            LoginScreen(
                viewModel = loginViewModel,
                abrirRegistro = { navController.navigate("registro") },
                abrirInicio = {
                    incidenciaViewModel.correoUsuarioActual = loginViewModel.correo.trim().lowercase()
                    loginViewModel.contrasena = ""
                    incidenciaViewModel.cargarIncidencias()
                    navController.navigate("inicio") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }
        composable("registro") {
            RegistroScreen(viewModel = registroViewModel,
                volverLogin = { navController.popBackStack() })
        }
        composable("inicio") {
            InicioScreen(
                nombreUsuario = loginViewModel.nombreUsuario,
                abrirNuevaIncidencia = { navController.navigate("nueva") },
                verIncidencias = { navController.navigate("incidencias") },
                abrirPerfil = { navController.navigate("perfil") },
                cerrarSesion = { cerrarSesion() }
            )
        }
        composable("nueva") {
            NuevaIncidenciaScreen(viewModel = incidenciaViewModel,
                volverInicio = { navController.popBackStack() })
        }
        composable("incidencias") {
            MisIncidenciasScreen(
                viewModel = incidenciaViewModel,
                volverInicio = { navController.popBackStack() },
                abrirDetalle = {
                    incidenciaViewModel.errorGestion = ""
                    navController.navigate("detalle/${it.id}")
                }
            )
        }
        composable("detalle/{id}", arguments = listOf(navArgument("id") { type = NavType.LongType })) { pantalla ->
            DetalleIncidenciaScreen(
                incidenciaId = pantalla.arguments?.getLong("id") ?: 0L,
                viewModel = incidenciaViewModel,
                volver = { navController.popBackStack() }
            )
        }
        composable("perfil") {
            MiPerfilScreen(
                nombreUsuario = loginViewModel.nombreUsuario,
                volverInicio = { navController.popBackStack() },
                cerrarSesion = { cerrarSesion() }
            )
        }
    }
}
