# Adaptación al estilo de clase — 28 de octubre de 2026

Se conserva el avance existente y se adapta su estructura a los ejemplos públicos actuales de amaurelia/basic-app. No se implementan GPS, mapa, filtros ni roles. Integrantes y Trello permanecen en blanco a petición de la usuaria.

## Cambios para estudiar

| Antes | Ahora | Referencia del profesor |
|---|---|---|
| Pantalla seleccionada con enum y when | Navegacion.kt con NavHost y NavController | ui/Navegacion.kt |
| Consultas observadas directamente como Flow de Room | DAO suspend devuelve List; ViewModel carga MutableStateFlow y recarga tras guardar/actualizar | CancionDao, CancionRepository y CancionViewModel |
| Botón construye argumentos de la acción municipal | Botón llama asignarInspector o cerrarIncidencia; la lógica queda en ViewModel | Separación de responsabilidades MVVM |
| Tres fábricas de ViewModel repetidas | Una sola fábrica compartida | Adaptación necesaria para conservar el estado al rotar |
| Conversión del hash con bits | Formato hexadecimal en una línea, compatible con los hashes anteriores | Mejora propia; no pertenece al login básico del profesor |

El login del profesor solo muestra un diálogo; CuidApp conserva el guardado de usuarios en Room que recomienda la pauta del docente. El formulario y las validaciones existentes se mantienen. No se guardan contraseñas en texto plano para hacer el código aparentemente más corto.

## Qué permanece y por qué

- Las migraciones existentes mantienen las cuentas e incidencias de instalaciones anteriores. No se reinicia la base ni se elimina información para reducir líneas.
- TakePicture, permiso y FileProvider corresponden al ejemplo CamaraFotos. La reducción de la foto se mantiene como ayuda propia porque evita imágenes enormes guardadas en Room.
- viewModels y una fábrica pequeña: el MainActivity del profesor crea los ViewModels con lazy, pero copiar eso haría perder el borrador al recrear la actividad. Esta es una diferencia deliberada, no algo atribuido al profesor.
- Las pruebas se conservan fuera del código de producción. No son nuevas funciones de la app.

## Cómo defenderlo

1. MainActivity prepara las dependencias y llama Navegacion.
2. NavHost declara las rutas; navigate abre una pantalla y popBackStack vuelve.
3. La pantalla recibe datos y acciones, y muestra los errores del ViewModel.
4. El ViewModel valida y usa viewModelScope para llamar al repositorio.
5. El repositorio delega en el DAO; Room guarda los datos en el dispositivo.
6. Después de guardar o actualizar se recarga la lista, igual que en el ejemplo de canciones.

No afirmar que todo se copió del profesor: se adapta su patrón al caso CuidApp. Rutas de detalle con ID, historial, hash, migraciones y reducción de fotos son adaptaciones del proyecto.

Este avance se guarda y publica a petición de la usuaria.

## Comprobación de esta adaptación

- assembleDebug: correcto.
- testDebugUnitTest: nueve pruebas aprobadas (siete de incidencias y dos de usuarios). Incluye compatibilidad con un hash anterior y rechazo de correo duplicado.
- lintDebug: sin errores; se conservan advertencias de configuración/dependencias existentes.
- connectedDebugAndroidTest: un recorrido completo aprobado en Medium Phone API 37.0, con registro, login, guardado, lista, detalle, asignación, cierre, logout/reingreso y recreación de actividad.
- git diff --check: correcto.

La prueba usa una cuenta ficticia y deja sus registros en la base del emulador; no elimina datos previos. No verifica captura de cámara ni todas las migraciones de versiones antiguas. No se dan por terminados los pendientes de la pauta.
