# CuidApp

Avance académico de Desarrollo de Aplicaciones Móviles, Duoc UC. Representa el registro y seguimiento de incidencias de seguridad comunal con datos ficticios. No es una aplicación municipal productiva.

**Integrantes:**

- Cristian Rivera
- Luciana Villarroel

**Trello:** [Tablero del equipo](https://trello.com/b/69vvkUwC/mi-tablero-de-trello)

## Lo implementado

- Registro y login con cuentas locales en Room, errores por campo y carga visual.
- Inicio y perfil con nombre de prueba y cierre de sesión.
- Incidencias con categoría, descripción, prioridad, sector ficticio, fotografía opcional y ubicación GPS opcional.
- Guardado en Room, lista de reportes propios y de otros usuarios ficticios, y detalle actualizado después de guardar o gestionar.
- Asignación y cierre simulados con historial; no hay permisos por rol todavía.
- Imágenes, tarjetas, diálogos y animaciones sencillas de mensajes.

La revisión conserva este avance; mapa, filtros y permisos por rol siguen pendientes.

## Ejecutar y probar

Abrir esta carpeta en Android Studio, sincronizar Gradle con Java 21 y ejecutar `app` en un emulador o dispositivo API 24 o superior. Se mantienen las versiones originales del proyecto.

1. Registrar nombre y apellido ficticios, un correo de prueba y contraseña de seis caracteres o más, confirmada igual. Comprobar primero los errores con campos vacíos.
2. Iniciar sesión con la cuenta registrada; una contraseña incorrecta debe mostrar error.
3. Desde Inicio, abrir Reportar incidencia. Comprobar errores al enviar vacío y luego elegir categoría, descripción de 10 a 300 caracteres, prioridad y sector ficticio.
4. Opcionalmente tomar una foto de un objeto de prueba. Revisar tanto el permiso aceptado como rechazado y cancelar la cámara. No capturar información real de personas.
5. Opcionalmente pulsar Obtener ubicación en el formulario y permitir ubicación aproximada o precisa. Ver latitud y longitud, guardar y comprobarlas en detalle. Con permiso rechazado se permite guardar sin ubicación. La lectura tiene un límite de espera y se cancela al abandonar la pantalla.
6. Guardar, aceptar la confirmación y volver al inicio. En Mis incidencias y comunidad debe aparecer el reporte propio. Las incidencias de otras cuentas se muestran en Comunidad; no es un filtro geográfico.
7. Abrir detalle. Las acciones municipales son simuladas: asignar inspector y cerrar. El historial debe mostrar la creación y las acciones realizadas. Un caso cerrado conserva su información.
8. Girar el dispositivo en el formulario y en detalle; comprobar que conserva el borrador, foto, sesión y pantalla. La sesión se conserva al rotar, pero se solicita login cuando el proceso se termina.
9. Cerrar y abrir la app, ingresar de nuevo y comprobar que Room conserva los registros.
10. Cerrar sesión y entrar con otra cuenta ficticia: no debe heredar el borrador ni la foto de la cuenta anterior.

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

## Relación con los ejemplos del profesor

Repositorio revisado: [amaurelia/basic-app](https://github.com/amaurelia/basic-app).

- [Navegacion.kt](https://github.com/amaurelia/basic-app/blob/master/app/src/main/java/cl/duoc/basico/ui/Navegacion.kt): rememberNavController, NavHost, composable y navigate. CuidApp usa la versión 2.9.5 que declara el catálogo del profesor (su Gradle también incluye otra declaración antigua; no se copia esa duplicación). popBackStack vuelve a la pantalla anterior y popUpTo limpia el historial al entrar o salir de la sesión.

- [Inputs.kt](https://github.com/amaurelia/basic-app/blob/master/app/src/main/java/cl/duoc/basico/ui/Inputs.kt): formularios y selectores. Se adaptan las opciones al caso de incidencias.
- [UsuarioFormViewModel.kt](https://github.com/amaurelia/basic-app/blob/master/app/src/main/java/cl/duoc/basico/viewmodel/UsuarioFormViewModel.kt): validaciones fuera de la vista y expresión de correo. El mínimo de contraseña y la confirmación son reglas previas de CuidApp.
- [CamaraFotos.kt](https://github.com/amaurelia/basic-app/blob/master/app/src/main/java/cl/duoc/basico/ui/CamaraFotos.kt): permiso, TakePicture y FileProvider. CuidApp reduce la foto antes de guardarla en Room; no depende solamente del archivo temporal de caché.
- [CancionDao.kt](https://github.com/amaurelia/basic-app/blob/master/app/src/main/java/cl/duoc/basico/model/CancionDao.kt) y [CancionViewModel.kt](https://github.com/amaurelia/basic-app/blob/master/app/src/main/java/cl/duoc/basico/viewmodel/CancionViewModel.kt): DAO, repositorio y viewModelScope para persistir. La consulta de CuidApp muestra todos los reportes, sin el límite de tres canciones del ejemplo. El DAO devuelve List mediante suspend y el ViewModel recarga la lista después de los cambios, siguiendo ese ejemplo.
- [Animacion.kt](https://github.com/amaurelia/basic-app/blob/master/app/src/main/java/cl/duoc/basico/ui/Animacion.kt): indicador de carga. Aquí acompaña una operación real, sin una espera artificial.
- [Login.kt](https://github.com/amaurelia/basic-app/blob/master/app/src/main/java/cl/duoc/basico/ui/Login.kt): campos y modal. Ese ejemplo no autentica; la autenticación local con Room es un avance posterior propio de CuidApp, coherente con la recomendación del docente.

## Explicación oral

“NavHost define las pantallas y NavController permite ir y volver. La pantalla recibe los datos y muestra los errores. El ViewModel valida y llama al repositorio. El repositorio usa el DAO para leer o escribir en Room. Al guardar, el botón se bloquea para evitar toques repetidos. Después de guardar o actualizar, el ViewModel vuelve a consultar Room y actualiza la lista. La cámara entrega una foto de prueba, que reducimos para no guardar una imagen enorme. El botón de GPS pide permiso y obtiene una ubicación; sus coordenadas se guardan con la incidencia. Los estados y el historial representan una gestión simulada; los permisos por rol quedan pendientes.”

Las cuentas conservan el hash SHA-256 existente, no la contraseña original. La conversión a texto se expresa con formato hexadecimal para evitar operaciones de bits en ese método. Es el mecanismo sencillo ya existente de este prototipo local; no se presenta como autenticación de producción.

## GPS basado en el ejemplo del profesor

Referencia: [GPS.kt](https://github.com/amaurelia/basic-app/blob/master/app/src/main/java/cl/duoc/basico/ui/GPS.kt). Se conserva FusedLocationProviderClient y su dependencia play-services-location 21.0.1.

CuidApp usa getCurrentLocation para pedir una sola lectura (en vez de requestLocationUpdates del ejemplo), con un límite de 15 segundos. Los permisos preciso y aproximado se solicitan juntos para funcionar en Android actual. El usuario puede rechazar el permiso y guardar sin ubicación. No se usa ubicación en segundo plano ni se agrega mapa. El borrador conserva las coordenadas en el ViewModel y Room utiliza las columnas latitud/longitud ya existentes, sin cambiar el esquema.

Para demostrarlo, usar ubicación simulada del emulador y datos ficticios. Ver `docs/gps-2026-10-28.md`.

## Pauta y pendientes

| Requisito | Situación del avance |
|---|---|
| Formulario, errores por campo y lógica separada | Implementados; comprobaciones automatizadas de incidencias |
| Imagen y modal | Logo, foto opcional y diálogos |
| Animación | Carga de operaciones y aparición de mensajes |
| Room y MVVM | Implementados para usuarios e incidencias |
| Navegación entre vistas | Implementada; corregida conservación al recrear actividad |
| Recursos nativos | Cámara y GPS implementados; completar prueba manual de cámara y permisos en dispositivo |
| Git y Trello | Repositorio y tablero enlazados; mostrar participación real y tareas actualizadas |
| Vídeo corto | Pendiente de grabar por el equipo |

El mensaje del docente menciona al menos un recurso nativo, pero el PDF de Evaluación Parcial 2 exige dos en su requisito y en IE 2.4.1. Se implementan cámara y GPS; no se sustituye una lectura de GPS por coordenadas fijas en el código. En las pruebas se inyectan coordenadas ficticias desde el emulador. El mapa queda para otra clase. No se inventan evidencias de colaboración, fechas ni nombres.

## Comprobaciones

```powershell
./gradlew.bat :app:assembleDebug :app:testDebugUnitTest :app:lintDebug --no-configuration-cache
./gradlew.bat :app:connectedDebugAndroidTest --no-configuration-cache
```

El segundo comando requiere un dispositivo conectado y crea datos ficticios de prueba. Consultar `docs/adaptacion-clase-2026-10-28.md` para esta adaptación y `docs/revision-2026-10-27.md` para la revisión anterior.
