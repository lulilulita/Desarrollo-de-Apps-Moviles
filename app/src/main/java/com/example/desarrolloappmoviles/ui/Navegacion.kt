package com.example.desarrolloappmoviles.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.ui.Alignment
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
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
    if (loginViewModel.restaurandoSesion) {
        Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        return
    }
    if (loginViewModel.errorSesion.isNotEmpty()) {
        AlertDialog(
            onDismissRequest = { loginViewModel.errorSesion = "" },
            title = { Text("Sesión") },
            text = { Text(loginViewModel.errorSesion) },
            confirmButton = {
                TextButton(onClick = { loginViewModel.errorSesion = "" }) { Text("Aceptar") }
            }
        )
    }

    // Se elige una sola vez, después de consultar la sesión guardada en Room.
    val pantallaInicial = remember { if (loginViewModel.loginExitoso) "inicio" else "login" }
    val navController = rememberNavController()
    val entrada by navController.currentBackStackEntryAsState()
    val rutaActual = entrada?.destination?.route

    fun volverAlLogin() {
        incidenciaViewModel.limpiarFormulario()
        incidenciaViewModel.correoUsuarioActual = ""
        navController.navigate("login") {
            popUpTo(navController.graph.id) { inclusive = true }
            launchSingleTop = true
        }
    }

    LaunchedEffect(rutaActual, loginViewModel.loginExitoso) {
        if (loginViewModel.loginExitoso) {
            incidenciaViewModel.correoUsuarioActual = loginViewModel.correo.trim().lowercase()
        } else if (rutaActual != null && rutaActual != "login" && rutaActual != "registro") {
            volverAlLogin()
        }
    }

    NavHost(navController = navController, startDestination = pantallaInicial, modifier = modifier) {
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
                abrirPerfil = { navController.navigate("perfil") }
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
                cerrarSesion = { loginViewModel.cerrarSesion() }
            )
        }
    }
}
