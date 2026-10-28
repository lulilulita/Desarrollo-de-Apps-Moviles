package com.example.desarrolloappmoviles

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.espresso.Espresso.closeSoftKeyboard
import org.junit.Rule
import org.junit.Test

class FlujoCuidAppTest {
    @get:Rule val pantalla = createAndroidComposeRule<MainActivity>()

    @Test fun registroLoginGuardadoDetalleYRotacion() {
        val correo = "prueba${System.currentTimeMillis()}@ejemplo.com"
        tocar("¿No tienes cuenta? Regístrate", desplazar = true)
        escribir("Nombre", "Vecino")
        escribir("Apellido", "Ficticio")
        escribir("Correo electrónico", correo)
        escribir("Contraseña", "clave123")
        escribir("Confirmar contraseña", "clave123")
        closeSoftKeyboard()
        tocar("Crear cuenta", desplazar = true)
        esperar("Cuenta creada correctamente. Ahora puedes iniciar sesión.")
        tocar("¿Ya tienes cuenta? Inicia sesión", desplazar = true)
        escribir("Correo electrónico", correo)
        escribir("Contraseña", "clave123")
        closeSoftKeyboard()
        tocar("Ingresar", desplazar = true)
        esperar("Inicio de sesión exitoso.")
        tocar("Continuar")
        tocar("Reportar incidencia", desplazar = true)
        tocar("Guardar incidencia", desplazar = true)
        pantalla.onNodeWithText("Describe la incidencia").assertExists()
        pantalla.onNodeWithText("Categoría").performScrollTo().performClick()
        pantalla.onNodeWithText("Iluminación").performClick()
        escribir("Descripción", "Prueba ficticia de luminaria apagada.")
        closeSoftKeyboard()
        pantalla.activityRule.scenario.recreate()
        pantalla.onNodeWithText("Prueba ficticia de luminaria apagada.").assertExists()
        pantalla.onNodeWithText("Sector ficticio").performScrollTo().performClick()
        pantalla.onNodeWithText("Sector A").performClick()
        tocar("Guardar incidencia", desplazar = true)
        esperar("Incidencia registrada")
        tocar("Aceptar")
        tocar("Volver al inicio", desplazar = true)
        tocar("Mis incidencias y comunidad", desplazar = true)
        esperar("Iluminación")
        pantalla.onNodeWithText("Iluminación").performScrollTo().performClick()
        esperar("Detalle de incidencia")
        pantalla.activityRule.scenario.recreate()
        esperar("Detalle de incidencia")
        pantalla.onNodeWithText("Prueba ficticia de luminaria apagada.").assertExists()
        tocar("Asignar inspector", desplazar = true)
        esperar("Estado: En revisión")
        tocar("Cerrar caso", desplazar = true)
        tocar("Sí, cerrar")
        esperar("Estado: Cerrada")
        pantalla.onNodeWithText("Asignar inspector").assertIsNotEnabled()
        tocar("Volver a incidencias", desplazar = true)
        tocar("Volver al inicio", desplazar = true)
        tocar("Mi perfil", desplazar = true)
        tocar("Cerrar sesión", desplazar = true)
        tocar("Sí, cerrar sesión")
        pantalla.onNodeWithText("Iniciar sesión").assertExists()
        escribir("Correo electrónico", correo)
        escribir("Contraseña", "clave123")
        closeSoftKeyboard()
        tocar("Ingresar", desplazar = true)
        esperar("Inicio de sesión exitoso.")
        tocar("Continuar")
        tocar("Mis incidencias y comunidad", desplazar = true)
        esperar("Iluminación")
        pantalla.onNodeWithText("Cerrada").assertExists()
    }

    private fun escribir(etiqueta: String, valor: String) {
        pantalla.onNodeWithText(etiqueta).performScrollTo().performTextInput(valor)
    }

    private fun tocar(texto: String, desplazar: Boolean = false) {
        val nodo = pantalla.onNode(hasText(texto) and hasClickAction())
        if (desplazar) nodo.performScrollTo()
        nodo.performClick()
    }

    private fun esperar(texto: String) {
        pantalla.waitUntil(15_000) {
            pantalla.onAllNodesWithText(texto).fetchSemanticsNodes().isNotEmpty()
        }
    }
}
