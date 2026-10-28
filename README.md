# CuidApp

Avance académico de Desarrollo de Aplicaciones Móviles, Duoc UC. Representa el registro y seguimiento de incidencias de seguridad comunal con datos ficticios. No es una aplicación municipal productiva.

**Integrantes:**

**Trello:**

## Lo implementado

- Registro y login con cuentas locales en Room, errores por campo y carga visual.
- Inicio y perfil con nombre de prueba y cierre de sesión.
- Incidencias con categoría, descripción, prioridad, sector ficticio y fotografía opcional mediante cámara.
- Guardado en Room, lista de reportes propios y de otros usuarios ficticios, y detalle actualizado.
- Asignación y cierre simulados con historial; no hay permisos por rol todavía.
- Imágenes, tarjetas, diálogos y animaciones sencillas de mensajes.

La revisión conserva este avance; no completa GPS, mapa, filtros ni permisos por rol.

## Ejecutar y probar

Abrir esta carpeta en Android Studio, sincronizar Gradle con Java 21 y ejecutar `app` en un emulador o dispositivo API 24 o superior. Se mantienen las versiones originales del proyecto.

1. Registrar nombre y apellido ficticios, un correo de prueba y contraseña de seis caracteres o más, confirmada igual. Comprobar primero los errores con campos vacíos.
2. Iniciar sesión con la cuenta registrada; una contraseña incorrecta debe mostrar error.
3. Desde Inicio, abrir Reportar incidencia. Comprobar errores al enviar vacío y luego elegir categoría, descripción de 10 a 300 caracteres, prioridad y sector ficticio.
4. Opcionalmente tomar una foto de un objeto de prueba. Revisar tanto el permiso aceptado como rechazado y cancelar la cámara. No capturar información real de personas.
5. Guardar, aceptar la confirmación y volver al inicio. En Mis incidencias y comunidad debe aparecer el reporte propio. Las incidencias de otras cuentas se muestran en Comunidad; no es un filtro geográfico.
6. Abrir detalle. Las acciones municipales son simuladas: asignar inspector y cerrar. El historial debe mostrar la creación y las acciones realizadas. Un caso cerrado conserva su información.
7. Girar el dispositivo en el formulario y en detalle; comprobar que conserva el borrador, foto, sesión y pantalla. La sesión se conserva al rotar, pero se solicita login cuando el proceso se termina.
8. Cerrar y abrir la app, ingresar de nuevo y comprobar que Room conserva los registros.
9. Cerrar sesión y entrar con otra cuenta ficticia: no debe heredar el borrador ni la foto de la cuenta anterior.

No borrar datos ni desinstalar para probar una actualización: eso elimina la base local. Las migraciones existentes se conservaron para mantener los registros anteriores.

## Cómo está organizado (MVVM)

| Carpeta/archivo | Responsabilidad |
|---|---|
| `ui` | Pantallas Compose, controles, mensajes y acceso al lanzador de cámara |
| `viewmodel` | Validaciones, estado del formulario, sesión y coordinación del guardado |
| `repository` | Acceso a los DAO, sin controles visuales |
| `model` | Entidades de Room, DAO y definición de base de datos |
| `CuidAppApplication` | Una base local y sus repositorios; migraciones existentes |
| `MainActivity` | Conecta las pantallas; un enum evita combinaciones de booleanos |

Los ViewModels se obtienen mediante `viewModels` con una fábrica sencilla para entregarles el repositorio. No se usan frameworks de inyección de dependencias. Compose observa `mutableStateOf`; las listas y el detalle observan los Flow de Room.

## Relación con los ejemplos del profesor

Repositorio revisado: [amaurelia/basic-app](https://github.com/amaurelia/basic-app).

- [Inputs.kt](https://github.com/amaurelia/basic-app/blob/master/app/src/main/java/cl/duoc/basico/ui/Inputs.kt): formularios y selectores. Se adaptan las opciones al caso de incidencias.
- [UsuarioFormViewModel.kt](https://github.com/amaurelia/basic-app/blob/master/app/src/main/java/cl/duoc/basico/viewmodel/UsuarioFormViewModel.kt): validaciones fuera de la vista y expresión de correo. El mínimo de contraseña y la confirmación son reglas previas de CuidApp.
- [CamaraFotos.kt](https://github.com/amaurelia/basic-app/blob/master/app/src/main/java/cl/duoc/basico/ui/CamaraFotos.kt): permiso, TakePicture y FileProvider. CuidApp reduce la foto antes de guardarla en Room; no depende solamente del archivo temporal de caché.
- [CancionDao.kt](https://github.com/amaurelia/basic-app/blob/master/app/src/main/java/cl/duoc/basico/model/CancionDao.kt) y [CancionViewModel.kt](https://github.com/amaurelia/basic-app/blob/master/app/src/main/java/cl/duoc/basico/viewmodel/CancionViewModel.kt): DAO, repositorio y viewModelScope para persistir. La consulta de CuidApp muestra todos los reportes, sin el límite de tres canciones del ejemplo.
- [Animacion.kt](https://github.com/amaurelia/basic-app/blob/master/app/src/main/java/cl/duoc/basico/ui/Animacion.kt): indicador de carga. Aquí acompaña una operación real, sin una espera artificial.
- [Login.kt](https://github.com/amaurelia/basic-app/blob/master/app/src/main/java/cl/duoc/basico/ui/Login.kt): campos y modal. Ese ejemplo no autentica; la autenticación local con Room es un avance posterior propio de CuidApp, coherente con la recomendación del docente.

## Explicación oral

“La pantalla recibe los datos y muestra los errores. El ViewModel valida y llama al repositorio. El repositorio usa el DAO para leer o escribir en Room. Al guardar, el botón se bloquea para evitar toques repetidos. La lista observa la base y se actualiza. La cámara entrega una foto de prueba, que reducimos para no guardar una imagen enorme. Los estados y el historial representan una gestión simulada; los permisos por rol quedan pendientes.”

Las cuentas guardan un hash SHA-256, no la contraseña original. Es el mecanismo sencillo ya existente de este prototipo local; no se presenta como autenticación de producción.

## Pauta y pendientes

| Requisito | Situación del avance |
|---|---|
| Formulario, errores por campo y lógica separada | Implementados; comprobaciones automatizadas de incidencias |
| Imagen y modal | Logo, foto opcional y diálogos |
| Animación | Carga de operaciones y aparición de mensajes |
| Room y MVVM | Implementados para usuarios e incidencias |
| Navegación entre vistas | Implementada; corregida conservación al recrear actividad |
| Recursos nativos | Cámara implementada; falta un segundo recurso para lo que exige el PDF |
| Git y Trello | Git disponible; completar integrantes y tablero y mostrar participación real |
| Vídeo corto | Pendiente de grabar por el equipo |

El mensaje del docente menciona al menos un recurso nativo, pero el PDF de Evaluación Parcial 2 exige dos en su requisito y en IE 2.4.1. No se considera que el proyecto ya cumpla ese punto completo solo por tener cámara. GPS/mapa se dejan para otra clase. No se inventan evidencias de colaboración, fechas ni nombres.

## Comprobaciones

```powershell
./gradlew.bat :app:assembleDebug :app:testDebugUnitTest :app:lintDebug --no-configuration-cache
./gradlew.bat :app:connectedDebugAndroidTest --no-configuration-cache
```

El segundo comando requiere un dispositivo conectado y crea datos ficticios de prueba. Consultar `docs/revision-2026-10-27.md` para los resultados y límites de esta revisión.
