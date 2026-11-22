# CuidApp

Avance académico de Desarrollo de Aplicaciones Móviles, Duoc UC. Representa el registro y seguimiento de incidencias de seguridad comunal con datos ficticios. No es una aplicación municipal productiva.

**Integrantes:**

- Cristian Rivera
- Luciana Villarroel

**Trello:** [Tablero del equipo](https://trello.com/b/69vvkUwC/mi-tablero-de-trello)

## Lo implementado

- Registro y login con cuentas locales en Room, errores por campo y carga visual.
- Inicio y perfil con nombre de prueba, sesión recordada y cierre de sesión.
- Incidencias con categoría, descripción, prioridad, sector ficticio y ubicación GPS opcional. La cámara permite adjuntar una fotografía durante el registro.
- Incidencias compartidas mediante Xano; Room conserva las cuentas y la sesión local.
- Asignación y cierre simulados con historial; no hay permisos por rol todavía.
- Imágenes, tarjetas, diálogos y animaciones sencillas de mensajes.

Esta es una versión académica de avance; el mapa y los permisos por rol siguen pendientes. Los filtros de incidencias están implementados. Login, Registro, Inicio y Perfil se mantienen simples, con el cierre de sesión en Perfil. La foto se adjunta durante el formulario, pero todavía no se sube a Xano.

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

Para pasos de ejecución, comandos de compilación y pruebas, arquitectura explicada y preguntas para la defensa, consulta la [guía de estudio de la Evaluación 3](docs/guia-estudio-cuidapp.md).

No borrar datos ni desinstalar para probar una actualización: eso elimina la base local. Las migraciones existentes se conservaron para mantener los registros anteriores.

## Cómo está organizado (MVVM)

| Carpeta/archivo | Responsabilidad |
|---|---|
| `ui` | Pantallas Compose, controles, mensajes y acceso al lanzador de cámara |
| `viewmodel` | Estado de pantalla y coordinación de operaciones |
| `repository` | Validaciones y acceso a Room o a Xano mediante Retrofit |
| `model` | Entidades de Room, DAO y definición de base de datos |
| `CuidAppApplication` | Una base local y sus repositorios; migraciones existentes |
| `MainActivity` | Crea los ViewModels y muestra la navegación |
| `ui/Navegacion.kt` | Rutas con NavHost, composable, navigate y popBackStack, como el profesor |

Los ViewModels se obtienen mediante `viewModels` con una fábrica sencilla para entregarles el repositorio. No se usan frameworks de inyección de dependencias. Compose observa `mutableStateOf`; el ViewModel carga la lista desde el repository en un MutableStateFlow y la recarga después de guardar o actualizar, como CancionViewModel. Las pantallas la leen con collectAsState.

## API de Xano

La app usa Retrofit siguiendo el ejemplo de clase (`RetrofitInstance`, `XanoApiService`, modelo API y repository). La URL base del grupo se define en `XANO_BASE_URL` en `app/build.gradle.kts`. El grupo `CuidaApp` expone siete endpoints: `GET /incidencias`, `POST /incidencias`, `GET /incidencias/{incidencias_id}`, `PUT /incidencias/{incidencias_id}`, `PATCH /incidencias/{incidencias_id}`, `DELETE /incidencias/{incidencias_id}` y `GET /incidencias-por-estado?estado=...`.

La app utiliza las siete rutas: lista y detalle (GET), filtro por estado (GET), creación (POST), asignación (PUT), cierre (PATCH) y eliminación (DELETE). El filtro está en la lista comunitaria y permite ver todos, ingresados, en revisión o cerrados. Desde “Mis incidencias” se puede eliminar un reporte propio luego de confirmar. Los campos enviados coinciden con la tabla Xano: categoría, descripción, prioridad, sector, estado, responsable, fecha, correo, coordenadas como JSON e historial. La fotografía se captura en el dispositivo, pero no se sube a Xano en esta versión.

Las validaciones de correo, contraseña, registro e incidencia viven en los repositories, según la indicación del docente. Los ViewModels conservan el estado de la pantalla y coordinan las operaciones.

## Generar APK release firmado

La firma se configura localmente y no se sube a Git. Crea `keystore.properties` en la raíz con `storeFile=nombre-keystore.jks`, `storePassword=...`, `keyAlias=...` y `keyPassword=...`; guarda el `.jks` en la raíz (ambos están excluidos por `.gitignore`). Luego ejecuta `./gradlew.bat :app:assembleRelease`. El APK se genera en `app/build/outputs/apk/release/`. Sin una keystore válida, Gradle no podrá generar un APK firmado.

## Sesión recordada con Room

Room guarda un campo sesionActiva en la cuenta que inició sesión. Al abrir la app, el ViewModel lee esa cuenta y la navegación empieza en Inicio si encuentra una sesión activa. Cerrar sesión pone la marca a cero; al volver a abrir pide login. No se vuelve a guardar la contraseña para restaurar la sesión ni se agrega otra dependencia.

Persistencia.kt del profesor muestra remember y una alternativa rememberSaveable para un contador: eso no guarda una sesión tras terminar el proceso. CuidApp adapta el Room ya usado en sus ejemplos. No se atribuye al profesor una sesión que su repo no implementa. Ver `docs/revision-simple-2026-10-29.md`.

Al actualizar desde la versión anterior, iniciar sesión una vez para que se guarde la marca. No se borran cuentas ni incidencias al migrar de Room 6 a 7. Borrar datos o desinstalar sí elimina la base local.

## Cobertura de pruebas unitarias

Generar el reporte JaCoCo con:

```powershell
./gradlew.bat :app:createDebugUnitTestCoverageReport
```

El reporte HTML queda en `app/build/reports/coverage/test/debug/index.html`. En la medición del 22-11-2026, los paquetes de lógica `viewmodel` y `repository` alcanzaron en conjunto 94,7% de instrucciones, 93,5% de líneas y 84,1% de ramas. El porcentaje se calcula sobre esos paquetes, que contienen la lógica de negocio evaluada; no incluye las pantallas Compose ni la infraestructura de Android/Room.

## Requisitos de Evaluación 3

| Requisito | Situación del avance |
|---|---|
| Formulario, errores por campo y validaciones en repository | Implementados; pruebas unitarias |
| Imagen y modal | Logo, foto opcional y diálogos |
| Animación | Carga de operaciones y aparición de mensajes |
| Room y MVVM | Room para cuentas y sesión; incidencias remotas mediante Xano y Retrofit |
| Navegación entre vistas | Implementada; corregida conservación al recrear actividad |
| Recursos nativos | Cámara y GPS implementados; cámara probada con permiso concedido y denegado |
| API Xano | Siete endpoints conectados desde la app: GET lista/detalle/filtro, POST, PUT, PATCH y DELETE |
| APK release firmado | Configuración de firma local; genera el APK con `./gradlew.bat :app:assembleRelease` |
| README técnico | Documentación de arquitectura, rutas, prueba y firma actualizada |
| Pruebas unitarias | 29 pruebas unitarias; reporte JaCoCo con cobertura de la lógica Repository/ViewModel sobre 80% |
| Git y Trello | Repositorio y tablero enlazados; el equipo debe subir cambios y reflejar tareas reales |

La pauta compartida para esta evaluación solicita 2 recursos nativos; CuidApp implementa cámara y GPS. No se inventan evidencias de colaboración, fechas ni nombres. El equipo debe mantener Trello actualizado y hacer push de los commits acordados.

## Comprobaciones

```powershell
./gradlew.bat :app:assembleDebug :app:testDebugUnitTest :app:lintDebug --no-configuration-cache
./gradlew.bat :app:connectedDebugAndroidTest --no-configuration-cache -Pandroid.injected.androidTest.leaveApksInstalledAfterRun=true
```

El segundo comando requiere un dispositivo conectado, crea datos ficticios de prueba y conserva la app instalada después de ejecutarla. Consultar `docs/adaptacion-clase-2026-10-28.md` para esta adaptación y `docs/revision-2026-10-27.md` para la revisión anterior.
