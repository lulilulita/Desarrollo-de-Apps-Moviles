# Sesión y revisión del código — 29 de octubre de 2026

## Alcance y criterio

Se revisaron los archivos Kotlin de producción, pruebas, Gradle, manifiesto y recursos XML; se contrastaron con el PDF Ev. Parcial 2_Estudiante_Encargo_DSY1105 y con los ejemplos actuales del profesor. La revisión comprueba referencias, responsabilidades, estado y funciones; no equivale a garantizar que no quede ningún defecto ni a conocer todo lo que el profesor explicó oralmente.

Se aplica Room, Compose, Material 3, ViewModel y Navigation Compose, que ya están en la materia. Se conserva el avance existente y no se completan mapa, filtros ni permisos por rol. No se hace commit ni push.

## Persistencia de la sesión (cambio solicitado)

Persistencia significa conservar datos al cerrar el proceso. Las cuentas y los reportes ya se guardaban, pero la identidad conectada estaba solo en memoria.

Se añade sesionActiva en UsuarioEntity; una consulta marca la cuenta elegida y desmarca las demás. Al abrir se consulta esa cuenta y se llega a Inicio. Cerrar sesión borra la marca. No se guarda otra contraseña ni se agrega otra biblioteca. La migración 6→7 añade únicamente esa columna.

El repositorio del profesor no tiene esta función lista: Persistencia.kt presenta un contador con remember y una alternativa rememberSaveable. Se adapta Room de sus ejemplos de canciones porque esos estados no conservan una sesión después de terminar el proceso. Esta diferencia se declara expresamente para la defensa.

## Revisión por archivo

| Archivo | Decisión y responsabilidad |
|---|---|
| `CuidAppApplication.kt` | Una base Room, dos repositorios y migraciones necesarias para conservar instalaciones anteriores. |
| `MainActivity.kt` | Prepara los ViewModels con una fábrica pequeña. Se conserva para mantener el estado al rotar. |
| `model/CuidAppDatabase.kt` | Definición de Room; versión 7 por el nuevo campo de sesión. |
| `model/IncidenciaDao.kt` | Consultar, insertar y actualizar, como el DAO de canciones. |
| `model/IncidenciaEntity.kt` | Datos del formulario, foto, coordenadas e historial existentes. |
| `model/UsuarioDao.kt` | Consultas sencillas para registrar, buscar y recordar/cerrar la sesión. |
| `model/UsuarioEntity.kt` | Datos de cuenta y marca sesionActiva; sin agregar otra tabla. |
| `repository/IncidenciaRepository.kt` | Delegación breve al DAO; sin lógica visual. |
| `repository/UsuarioRepository.kt` | Coordina el DAO; mantiene el hash existente y la sesión local. |
| `ui/DetalleIncidenciaScreen.kt` | Muestra el reporte, foto, coordenadas e historial; gestión simulada, sin roles reales. |
| `ui/GPS.kt` | Proveedor del profesor, permisos y una lectura limitada; cancelación y errores evitan solicitudes abiertas. |
| `ui/InicioScreen.kt` | Simplificado: logo, saludo y tres acciones. Retiradas tarjetas decorativas y cierre duplicado. |
| `ui/LoginScreen.kt` | Campos y modal, sin SQL ni validaciones de negocio dentro de la vista. |
| `ui/MiPerfilScreen.kt` | Simplificado: nombre, rol referencial, cierre con modal y volver. Retirada tarjeta informativa repetida. |
| `ui/MisIncidenciasScreen.kt` | Lista y pestañas de cuentas ficticias; se conserva por el mockup. |
| `ui/Navegacion.kt` | NavHost/composable/navigate/popBackStack como el ejemplo; control mínimo de sesión. |
| `ui/NuevaIncidenciaScreen.kt` | Formulario y lanzador de cámara; sustituida una tarjeta redundante por texto. |
| `ui/RegistroScreen.kt` | Campos, errores e iconos; animación sencilla de mensajes. |
| `viewmodel/IncidenciaViewModel.kt` | Validaciones, recarga de lista y acciones simuladas del caso. |
| `viewmodel/LoginViewModel.kt` | Validación y estado; recupera y borra la marca de sesión mediante el repositorio. |
| `viewmodel/RegistroViewModel.kt` | Validaciones de cuenta y guardado con carga y errores. |

## Qué se simplificó y qué se conserva

Se retiraron las tarjetas de texto repetido en Inicio y Perfil y el cierre duplicado de Inicio. Cerrar sesión queda en Mi perfil, como el mockup. Se limpiaron comentarios de plantilla XML e imports/namespace sin uso. La caché .kotlin queda ignorada.

Se conservan las migraciones: quitarlas reiniciando la base para reducir líneas perdería datos. Se mantienen los controles de permisos, errores y cancelación de cámara/GPS. Son soporte de un recurso nativo funcional, no funciones extra. Se mantienen pruebas fuera de la aplicación; no añaden pantallas ni servicios.

El hash, la reducción de fotos, las migraciones y la pequeña fábrica de ViewModels son adaptaciones de soporte de CuidApp; no aparecen idénticas en el ejemplo de login del profesor ni constituyen apartados independientes de la rúbrica. No se incorporan servidores, tokens remotos, inyección de dependencias externa ni arquitectura por módulos.

## Relación con la pauta

- Formularios e iconos: registro, login y nueva incidencia.
- Lógica desacoplada: validaciones en ViewModels, acceso a datos en repositorios/DAO.
- Diseño/navegación: tarjetas Material 3 y rutas de Navigation Compose.
- Animación y modal: carga, aparición de mensajes y diálogos.
- Persistencia y MVVM: Room para cuentas, incidencias y ahora sesión.
- Recursos nativos: cámara y GPS implementados; revisar sus permisos en dispositivo real.
- Colaboración: integrantes y Trello enlazados; la participación real y la entrega en AVA corresponden al equipo.

El PDF solicita dos recursos nativos y restringe cambios publicados después de la entrega. Esta revisión no publica nada ni inventa evidencias de colaboración. Tener los componentes implementados no permite prometer una nota ni afirmar que todos los requisitos del caso estén terminados.

## Referencias revisadas

- https://github.com/amaurelia/basic-app/blob/master/app/src/main/java/cl/duoc/basico/ui/Persistencia.kt
- https://github.com/amaurelia/basic-app/blob/master/app/src/main/java/cl/duoc/basico/ui/Navegacion.kt
- https://github.com/amaurelia/basic-app/blob/master/app/src/main/java/cl/duoc/basico/viewmodel/CancionViewModel.kt
- https://github.com/amaurelia/basic-app/blob/master/app/src/main/java/cl/duoc/basico/repository/FormularioRepository.kt
- https://github.com/amaurelia/basic-app/blob/master/app/src/main/java/cl/duoc/basico/ui/CamaraFotos.kt
- https://github.com/amaurelia/basic-app/blob/master/app/src/main/java/cl/duoc/basico/ui/GPS.kt

## Prueba manual para clase

1. Actualizar la app e iniciar sesión una vez.
2. Cerrarla completamente y volver a abrir: debe mostrar Inicio con la misma cuenta.
3. En Mi perfil pulsar Cerrar sesión y confirmar.
4. Cerrar y abrir otra vez: ahora debe aparecer el login.
5. Ingresar con otra cuenta ficticia y verificar sus incidencias propias.

Explicación oral: “Room guarda mis datos aunque cierre la app. Además de la cuenta, guardo si su sesión está activa. Al iniciar consulto esa marca; al cerrar sesión la quito. El ViewModel decide el estado y la navegación muestra la pantalla correspondiente.”

## Resultados comprobados

- Compilación assembleDebug correcta y Lint sin errores. Se mantienen avisos existentes de versión de SDK/dependencias.
- Trece pruebas unitarias aprobadas (nueve de incidencias y cuatro de usuario/sesión).
- Recorrido instrumentado aprobado después de simplificar Inicio y Perfil: registro, login, incidencia, detalle, rotación, gestión y cierre/reingreso.
- Prueba de cerrar sesión recordada aprobada.
- Prueba externa al proceso de tests: force-stop y reapertura mostraron Inicio sin ingresar credenciales; después de Cerrar sesión, force-stop y reapertura mostraron Iniciar sesión.
- La ejecución de pruebas conserva ahora el APK mediante una opción del comando de Gradle; sin ella el ejecutor lo desinstala al terminar. Eso es configuración de pruebas, no código añadido a la aplicación.
- git diff --check correcto. El análisis de referencias no dejó imports explícitos sin uso en Kotlin de producción.
- Se revisaron 21 archivos Kotlin de producción. Pasaron de 3102 líneas al comienzo de esta revisión a 2516, aun incorporando la recuperación de sesión; la reducción proviene principalmente de UI repetida.

Las comprobaciones de esta revisión no equivalen a probar todas las combinaciones de permisos de cámara/GPS, todas las migraciones históricas ni todos los modelos de teléfono. Esas funciones existentes se conservaron; no se reimplementaron para aparentar una copia literal del profesor.

## Segunda comparación completa con basic-app

Referencia descargada de nuevo el 29 de octubre: commit `3a1c5e3` (rama master). Se leyeron los 38 archivos Kotlin de producción, las dos pruebas de plantilla, manifiesto, recursos XML, catálogos, configuración Gradle y README. No se considera que los metadatos de Git, las imágenes o las preferencias del IDE sean lógica de la aplicación.

- **Datos:** AppDatabase, Cancion, CancionDao y CancionRepository muestran Room, consultas suspend y un repositorio sencillo. CuidApp conserva ese recorrido para usuarios e incidencias.
- **Estado y validación:** se revisaron todos los modelos, repositorios y ViewModels, incluidos los ejemplos de formulario repetidos y las clases vacías. Se conservan validaciones explícitas en CuidApp; no se copian clases vacías, registros de depuración ni código inalcanzable del material de ejercicios.
- **Pantallas:** se revisaron los 16 ejemplos de UI y los tres archivos de tema: formularios, usuarios, login, navegación, persistencia, animación, modal, inputs, canciones, cámara, GPS, mapa, texto invertido y juego táctil. Cada ejemplo es independiente; no corresponde añadirlos todos al proyecto.
- **Configuración:** se compararon permisos, FileProvider y dependencias. Se conserva la configuración compatible de CuidApp y no se duplican versiones de navegación.

En esta segunda pasada se simplifican también Login y Registro: se retiran cabeceras con tarjetas y contenedores repetidos, manteniendo imagen, iconos, campos, errores, carga, modal y animación. No se crean componentes genéricos nuevos. Se elimina `sesionRestaurada`: después de leer Room se elige Inicio o Login como destino inicial, sin otra bandera ni redirección especial para restaurar.

La sesión sigue siendo una adaptación propia usando Room del profesor. Su Login.kt no comprueba usuarios y su UsuarioRepository guarda una lista solo en memoria; copiarlo literalmente haría perder el registro persistente. Se conserva el hash compatible con las cuentas existentes, las migraciones, el manejo de permisos y el soporte de rotación. No se agregan funciones, pantallas ni dependencias.
