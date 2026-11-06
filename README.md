# CuidApp

Avance académico de Desarrollo de Aplicaciones Móviles, Duoc UC. Representa el registro y seguimiento de incidencias de seguridad comunal con datos ficticios. No es una aplicación municipal productiva.

**Integrantes:**

- Cristian Rivera
- Luciana Villarroel

**Trello:** [Tablero del equipo](https://trello.com/b/69vvkUwC/mi-tablero-de-trello)

## Lo implementado

- Registro y login con cuentas locales en Room, errores por campo y carga visual.
- Inicio y perfil con nombre de prueba, sesión recordada y cierre de sesión.
- Incidencias con categoría, descripción, prioridad, sector ficticio, fotografía opcional y ubicación GPS opcional.
- Guardado en Room, lista de reportes propios y de otros usuarios ficticios, y detalle actualizado después de guardar o gestionar.
- Asignación y cierre simulados con historial; no hay permisos por rol todavía.
- Imágenes, tarjetas, diálogos y animaciones sencillas de mensajes.

Esta es una versión académica de avance; mapa, filtros y permisos por rol siguen pendientes. Login, Registro, Inicio y Perfil se mantienen simples, con el cierre de sesión en Perfil.

## Ejecutar y probar

Abrir esta carpeta en Android Studio, sincronizar Gradle con Java 21 y ejecutar `app` en un emulador o dispositivo API 24 o superior. Se mantienen las versiones originales del proyecto.

1. Registrar nombre y apellido ficticios, un correo de prueba y contraseña de seis caracteres o más, confirmada igual. Comprobar primero los errores con campos vacíos.
2. Iniciar sesión con la cuenta registrada; una contraseña incorrecta debe mostrar error.
3. Desde Inicio, abrir Reportar incidencia. Comprobar errores al enviar vacío y luego elegir categoría, descripción de 10 a 300 caracteres, prioridad y sector ficticio.
4. Opcionalmente tomar una foto de un objeto de prueba. Revisar tanto el permiso aceptado como rechazado y cancelar la cámara. No capturar información real de personas.
5. Opcionalmente pulsar Obtener ubicación en el formulario y permitir ubicación aproximada o precisa. Ver latitud y longitud, guardar y comprobarlas en detalle. Con permiso rechazado se permite guardar sin ubicación. La lectura tiene un límite de espera y se cancela al abandonar la pantalla.
6. Guardar, aceptar la confirmación y volver al inicio. En Mis incidencias y comunidad debe aparecer el reporte propio. Las incidencias de otras cuentas se muestran en Comunidad; no es un filtro geográfico.
7. Abrir detalle. Las acciones municipales son simuladas: asignar inspector y cerrar. El historial debe mostrar la creación y las acciones realizadas. Un caso cerrado conserva su información.
8. Girar el dispositivo en el formulario y en detalle; comprobar que conserva el borrador, foto, sesión y pantalla. La sesión se conserva al rotar y al terminar el proceso: vuelve a Inicio mientras no se pulse Cerrar sesión.
9. Cerrar completamente y abrir la app: debe entrar a Inicio con la misma cuenta y conservar los registros.
10. En Mi perfil, cerrar sesión y entrar con otra cuenta ficticia: no debe heredar el borrador ni la foto de la cuenta anterior.

No borrar datos ni desinstalar para probar una actualización: eso elimina la base local. Las migraciones existentes se conservaron para mantener los registros anteriores.

## Cómo está organizado (MVVM)

| Carpeta/archivo | Responsabilidad |
|---|---|
| `ui` | Pantallas Compose, controles, mensajes y acceso al lanzador de cámara |
| `viewmodel` | Validaciones, estado del formulario, sesión y coordinación del guardado |
| `repository` | Acceso a los DAO, sin controles visuales |
| `model` | Entidades de Room, DAO y definición de base de datos |
| `CuidAppApplication` | Una base local y sus repositorios; migraciones existentes |
| `MainActivity` | Crea los ViewModels y muestra la navegación |
| `ui/Navegacion.kt` | Rutas con NavHost, composable, navigate y popBackStack, como el profesor |

Los ViewModels se obtienen mediante `viewModels` con una fábrica sencilla para entregarles el repositorio. No se usan frameworks de inyección de dependencias. Compose observa `mutableStateOf`; el ViewModel carga la lista desde Room en un MutableStateFlow y la recarga después de guardar o actualizar, como CancionViewModel. Las pantallas la leen con collectAsState.

## Sesión recordada con Room

Room guarda un campo sesionActiva en la cuenta que inició sesión. Al abrir la app, el ViewModel lee esa cuenta y la navegación empieza en Inicio si encuentra una sesión activa. Cerrar sesión pone la marca a cero; al volver a abrir pide login. No se vuelve a guardar la contraseña para restaurar la sesión ni se agrega otra dependencia.

Persistencia.kt del profesor muestra remember y una alternativa rememberSaveable para un contador: eso no guarda una sesión tras terminar el proceso. CuidApp adapta el Room ya usado en sus ejemplos. No se atribuye al profesor una sesión que su repo no implementa. Ver `docs/revision-simple-2026-10-29.md`.

Al actualizar desde la versión anterior, iniciar sesión una vez para que se guarde la marca. No se borran cuentas ni incidencias al migrar de Room 6 a 7. Borrar datos o desinstalar sí elimina la base local.

## Comprobaciones

```powershell
./gradlew.bat :app:assembleDebug :app:testDebugUnitTest :app:lintDebug --no-configuration-cache
./gradlew.bat :app:connectedDebugAndroidTest --no-configuration-cache -Pandroid.injected.androidTest.leaveApksInstalledAfterRun=true
```

El segundo comando requiere un dispositivo conectado, crea datos ficticios de prueba y conserva la app instalada después de ejecutarla. Consultar `docs/adaptacion-clase-2026-10-28.md` para esta adaptación y `docs/revision-2026-10-27.md` para la revisión anterior.
