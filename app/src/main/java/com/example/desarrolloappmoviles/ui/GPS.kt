package com.example.desarrolloappmoviles.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.desarrolloappmoviles.viewmodel.IncidenciaViewModel
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource

// Adaptación de GPS.kt del profesor: permiso, FusedLocationProviderClient y coordenadas.
@Composable
fun GPS(viewModel: IncidenciaViewModel) {
    val context = LocalContext.current
    val cliente = remember(context) { LocationServices.getFusedLocationProviderClient(context) }
    val cancelacion = remember { CancellationTokenSource() }
    var error by remember { mutableStateOf("") }

    // Se cancela la lectura al abandonar la pantalla; no hay seguimiento en segundo plano.
    DisposableEffect(Unit) {
        onDispose {
            cancelacion.cancel()
            viewModel.obteniendoUbicacion = false
        }
    }

    @SuppressLint("MissingPermission")
    fun obtenerUbicacion() {
        val permisoPreciso = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
        val permisoAproximado = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
        if (permisoPreciso != PackageManager.PERMISSION_GRANTED && permisoAproximado != PackageManager.PERMISSION_GRANTED) {
            error = "Permite la ubicación para obtener las coordenadas."
            return
        }
        error = ""
        viewModel.obteniendoUbicacion = true
        // Una lectura con límite de espera; no dejar una solicitud abierta si el GPS no responde.
        val solicitud = CurrentLocationRequest.Builder()
            .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            .setDurationMillis(15_000)
            .build()
        try {
            cliente.getCurrentLocation(solicitud, cancelacion.token)
                .addOnSuccessListener { ubicacion ->
                    if (!cancelacion.token.isCancellationRequested) {
                        viewModel.obteniendoUbicacion = false
                        if (ubicacion != null) {
                            viewModel.latitud = ubicacion.latitude
                            viewModel.longitud = ubicacion.longitude
                        } else {
                            error = "No se obtuvo ubicación. Actívala e intenta nuevamente."
                        }
                    }
                }
                .addOnFailureListener {
                    if (!cancelacion.token.isCancellationRequested) {
                        viewModel.obteniendoUbicacion = false
                        error = "No se pudo obtener la ubicación. Intenta nuevamente."
                    }
                }
        } catch (_: SecurityException) {
            viewModel.obteniendoUbicacion = false
            error = "El permiso de ubicación ya no está disponible."
        }
    }

    // Android permite elegir entre ubicación aproximada y precisa.
    val permisoUbicacion = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permisos ->
        if (permisos.values.any { it }) obtenerUbicacion()
        else error = "Permiso rechazado. Puedes guardar la incidencia sin ubicación."
    }

    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("Ubicación de prueba (opcional)", style = MaterialTheme.typography.titleMedium)
            Text("En el emulador usa coordenadas simuladas.", style = MaterialTheme.typography.bodySmall)
            Button(
                onClick = { permisoUbicacion.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)) },
                enabled = !viewModel.obteniendoUbicacion && !viewModel.guardando,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (viewModel.obteniendoUbicacion) {
                    CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp, color = LocalContentColor.current)
                } else Text("Obtener ubicación")
            }
            if (viewModel.latitud != null && viewModel.longitud != null) {
                Text("Latitud: ${viewModel.latitud}")
                Text("Longitud: ${viewModel.longitud}")
                TextButton(onClick = {
                    viewModel.latitud = null
                    viewModel.longitud = null
                    error = ""
                }, enabled = !viewModel.obteniendoUbicacion && !viewModel.guardando) { Text("Quitar ubicación") }
            }
            if (error.isNotEmpty()) Text(error, color = MaterialTheme.colorScheme.error)
        }
    }
}
