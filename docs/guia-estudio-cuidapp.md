# Guía de estudio y defensa de CuidApp — Evaluación 3

Guía para estudiar la versión actual del repositorio. Resume qué hace la app, cómo se conectan sus capas, cómo ejecutarla y qué conviene demostrar en la evaluación. No reemplaza las instrucciones oficiales del curso; se basa en los requisitos de Ev. 3 que compartió el equipo.

## 1. Qué es CuidApp

CuidApp permite registrar y consultar incidencias comunitarias. La app está desarrollada en Kotlin y Jetpack Compose. Room almacena cuentas y sesión local; las incidencias se consultan y guardan en Xano mediante Retrofit. Un formulario permite adjuntar una foto con la cámara y solicitar la ubicación del dispositivo.

La gestión de inspector y el cierre de casos son simulaciones para demostrar operaciones de actualización. El modelo no implementa control real de roles. La foto capturada se muestra como evidencia en el formulario, pero no se sube a Xano ni queda guardada en la tabla remota en esta versión.

## 2. Cómo ejecutar la app

### Android Studio

1. Abre la carpeta del repositorio en Android Studio y espera la sincronización de Gradle.
2. Inicia un emulador o conecta un teléfono con depuración USB habilitada.
3. Selecciona la configuración `app` y presiona **Run**.
4. Registra una cuenta de prueba, inicia sesión y recorre las pantallas.

La primera compilación puede tardar mientras Gradle descarga dependencias. La conexión a internet es necesaria para descargar esas dependencias y para usar los endpoints de Xano.

### PowerShell, desde la raíz del proyecto

```powershell
./gradlew.bat :app:assembleDebug
```

Para instalar y abrir la app en un dispositivo/emulador conectado:

```powershell
./gradlew.bat :app:installDebug
```

El APK de depuración queda en `app/build/outputs/apk/debug/app-debug.apk`.

### Pruebas

```powershell
# Pruebas unitarias locales
./gradlew.bat :app:testDebugUnitTest

# Pruebas instrumentadas; requiere emulador o dispositivo conectado
./gradlew.bat :app:connectedDebugAndroidTest

# Reporte de cobertura de pruebas unitarias
./gradlew.bat :app:createDebugUnitTestCoverageReport
```

El reporte instrumentado queda en `app/build/reports/androidTests/connected/debug/index.html`; la cobertura se genera bajo `app/build/reports/coverage/`. En la última ejecución instrumentada registrada en esta entrega pasaron 2 pruebas y no hubo fallas. También se comprobó manualmente la captura de cámara con permiso concedido y el mensaje al denegar el permiso.

### APK release firmado

La firma usa un archivo local `keystore.properties` y una keystore `.jks` guardada en la raíz. Esos archivos están excluidos de Git porque contienen credenciales privadas. Con la firma configurada:

```powershell
./gradlew.bat :app:assembleRelease
```

El APK queda en `app/build/outputs/apk/release/app-release.apk`. Si la keystore o sus datos no están configurados en el computador, primero hay que recuperarlos; no se debe crear una keystore nueva para una actualización si ya existe una usada anteriormente, porque Android no aceptará la actualización firmada con otra clave.

## 3. Arquitectura: quién hace qué

El flujo de una incidencia nueva es:

```text
NuevaIncidenciaScreen
    └─ entrega los campos al IncidenciaViewModel
          ├─ pide al IncidenciaRepository validar los datos
          └─ si son válidos, coordina el guardado en una corrutina
                 └─ IncidenciaRepository convierte el modelo y llama a XanoApiService
                        └─ Retrofit envía HTTP a Xano
```

| Capa/archivo | Responsabilidad en este proyecto |
|---|---|
| `ui/` | Compose dibuja pantallas, recoge interacción, muestra errores, estados y permisos nativos. No contiene las reglas de validación del negocio. |
| `viewmodel/` | Conserva el estado visible y coordina acciones asíncronas. Llama a repositories y expone resultados a Compose. |
| `repository/` | Contiene reglas de validación y acceso a datos. `IncidenciaRepository` usa Xano para incidencias; `UsuarioRepository` usa el DAO local. |
| `model/` | Entidades locales, DAO/base Room y modelo de intercambio con Xano. |
| `XanoApiService` | Declara rutas HTTP, verbos, parámetros y cuerpos que Retrofit ejecuta. |
| `RetrofitInstance` | Configura Retrofit, URL base y conversor JSON Gson. |
| `CuidAppApplication` | Prepara Room y crea los repositories; conecta el repository de incidencias con el servicio Xano. |

**Por qué separar capas:** una pantalla puede mostrar y recoger datos sin saber cómo se persisten; el ViewModel mantiene el estado de pantalla; el repository centraliza reglas y fuente de datos. Esto hace más claro el flujo y permite probar la lógica con dependencias sustitutas.

**Regla pedida por el profesor:** las validaciones están en repositories. El ViewModel pide los errores al repository y los distribuye a los campos de la pantalla; no define ahí reglas como correo válido o largo mínimo.

## 4. Datos locales y remotos

- **Room/SQLite local:** cuentas, hash de contraseña y marca de sesión activa. Por eso la sesión se recuerda en el mismo dispositivo.
- **Xano remoto:** incidencias compartidas. Distintos dispositivos conectados al mismo backend consultan la misma tabla.
- **Cámara:** entrega una URI temporal; la app reduce la imagen y la conserva como bytes en el formulario mientras se prepara el reporte. El modelo/API actual no carga esa imagen al backend.
- **GPS:** devuelve latitud y longitud opcionales; se envían en el objeto `coordenadas` de la incidencia.

El login/registro del proyecto sigue siendo local. Xano se utiliza para las incidencias, no como autenticación de usuarios.

## 5. Xano y los siete endpoints

La URL base se declara como `XANO_BASE_URL` en `app/build.gradle.kts`. `IncidenciaApiModel` traduce entre los nombres/tipos de Xano y `IncidenciaEntity`. Las siete rutas configuradas en `XanoApiService` son:

| Método | Ruta | Uso desde CuidApp |
|---|---|---|
| GET | `/incidencias` | Cargar lista comunitaria |
| POST | `/incidencias` | Crear una incidencia |
| GET | `/incidencias/{incidencias_id}` | Consultar el detalle por ID |
| PUT | `/incidencias/{incidencias_id}` | Reemplazar datos al asignar inspector de prueba |
| PATCH | `/incidencias/{incidencias_id}` | Actualizar parcialmente al cerrar un caso |
| DELETE | `/incidencias/{incidencias_id}` | Eliminar un reporte propio después de confirmar |
| GET | `/incidencias-por-estado?estado=...` | Filtrar por estado |

Así se cumple la variedad de métodos HTTP exigida: hay operaciones GET y POST, además de PUT, PATCH y DELETE. En una defensa, mostrar el grupo publicado en Xano y luego señalar `XanoApiService.kt` ayuda a relacionar endpoint con código. La app debe tener internet y el grupo/rutas deben seguir publicados.

**Términos:** endpoint es una ruta/operación disponible; GET consulta; POST crea; PUT reemplaza el registro; PATCH modifica parcialmente; DELETE elimina; JSON es el formato de intercambio; Retrofit realiza la petición y Gson convierte JSON a objetos Kotlin.

## 6. Funciones que conviene demostrar

1. Registro y login; la app normaliza el correo y guarda la sesión local.
2. Crear una incidencia con categoría, descripción de al menos 10 caracteres, prioridad y sector.
3. Pedir ubicación GPS y revisar que se reflejen coordenadas cuando el dispositivo entrega una ubicación.
4. Tomar una fotografía, aceptar la captura y mostrar la vista previa.
5. Abrir “Mis incidencias” y consultar la lista remota; filtrar por “Ingresada”, “En revisión” o “Cerrada”. “Todos” vuelve a consultar la lista general.
6. Abrir el detalle. En un reporte propio, probar eliminar y aceptar la confirmación. En una incidencia, mostrar asignación/cierre simulados si la cuenta y los datos de prueba permiten esa acción.
7. Cerrar y volver a abrir para mostrar la sesión guardada; cerrar sesión para mostrar el retorno al login.

No dependas de que Xano tenga registros de ejemplo. Si la tabla está vacía, registrar una incidencia durante la demostración. Las llamadas repetidas muy rápido pueden recibir limitación temporal del servicio (HTTP 429); espera antes de repetirlas.

## 7. Recursos nativos y permisos

**Cámara:** `NuevaIncidenciaScreen` solicita `Manifest.permission.CAMERA` durante el uso. Si se concede, crea un archivo temporal, lo comparte con `FileProvider` y abre `TakePicture`. Si se deniega, muestra un mensaje y permite seguir sin foto. Para demostrarla, usa un dispositivo/emulador con cámara disponible, concede el permiso, captura y acepta la foto.

**Ubicación:** `ui/GPS.kt` solicita permisos de ubicación y usa el proveedor de Google Play Services para obtener una lectura actual. No es seguimiento continuo. La ubicación es opcional: se puede registrar una incidencia sin ella.

Declarar permiso en `AndroidManifest.xml` no equivale a tenerlo concedido: Android también pide autorización en tiempo de ejecución. La evaluación requiere que el equipo pueda demostrar los dos recursos nativos; conviene verificar ambos en el mismo equipo que usarán para defender.

## 8. Pruebas y qué prueban

- `IncidenciaViewModelTest`: coordinación y estado del formulario, validación delegada, guardado, gestión y respuestas ante fallos con repository de prueba.
- `IncidenciaRepositoryTest`: reglas de incidencia y operaciones/mapeo usando un servicio sustituido.
- `UsuarioRepositoryTest` y `UsuarioFlujosTest`: reglas de registro/login, normalización, cuentas duplicadas, sesión y flujos locales.
- `FlujoCuidAppTest`: pruebas instrumentadas con UI real en un emulador; validan recorridos clave, incluyendo registro/login y creación/detalle/gestión.

Las pruebas unitarias no sustituyen una verificación real del backend ni de los permisos del dispositivo. La prueba instrumentada requiere emulador conectado. La verificación manual de cámara/GPS comprueba hardware/permisos que no se cubren completamente con pruebas locales.

## 9. Requisitos de Ev. 3: checklist de evidencia

| Requisito compartido | Evidencia preparada | Qué revisar antes de entregar |
|---|---|---|
| Al menos 7 endpoints, GET y POST | Siete rutas declaradas y consumidas desde la app | Xano publicado, tabla correcta y prueba real desde app |
| Al menos 2 pruebas unitarias | Hay varias pruebas unitarias; reporte y resultados quedan en Gradle | Ejecutar `:app:testDebugUnitTest` en el entorno de entrega |
| APK firmado release | Gradle tiene configuración de firma local | Generar APK release en el computador final y verificar firma/instalación |
| Repositorio actualizado | Código y documentación en Git | El equipo hará commits y push acordados; revisar rama y remoto |
| Trello actualizado | No puede probarse solo con el código | Equipo actualiza tarjetas según trabajo real |
| Documentación técnica en README | README describe arquitectura, Xano, ejecución, firma y pruebas | Leerlo una vez y corregir cualquier cambio de la configuración |
| APK con icono personalizado | Recursos de icono ya forman parte de la app | Instalar APK y revisar icono en launcher |
| Dos recursos nativos | Cámara y GPS | Demostrar permisos y uso en el dispositivo de defensa |
| App funcional/no debe fallar | Build y dos pruebas instrumentadas pasaron en emulador | Ejecutar también en el teléfono y comprobar red/Xano antes de presentar |
| MVVM y código legible | Validaciones en repository; UI y estado separados | Poder explicar el flujo y no atribuir acceso Xano a Room |

El encargo de Ev. 3 pide enviar únicamente el APK. README, repositorio y Trello sirven como documentación/evidencia para revisión, pero el archivo solicitado para la entrega práctica es el APK. El plazo y porcentaje deben confirmarse con la pauta vigente del curso.

## 10. Guion breve para la defensa

> “CuidApp registra incidencias comunitarias. La interfaz está hecha con Compose. La pantalla recoge los datos y presenta errores; el ViewModel mantiene el estado y coordina las operaciones; los repositories validan y acceden a los datos. Las cuentas y la sesión quedan en Room local. Las incidencias usan Retrofit para conectarse a Xano. El servicio tiene siete rutas: listar, crear, consultar por ID, reemplazar, actualizar parcialmente, eliminar y filtrar por estado. La cámara y la ubicación son recursos nativos opcionales. Las pruebas unitarias cubren la lógica y las pruebas instrumentadas ejecutan flujos de pantalla en Android.”

## 11. Preguntas para estudiar

1. **¿Qué guarda Room y qué guarda Xano?** Room mantiene usuarios/sesión en el teléfono; Xano almacena incidencias compartidas.
2. **¿Dónde están las validaciones?** En los repositories; el ViewModel las solicita y conserva el resultado para la pantalla.
3. **¿Qué hace un ViewModel?** Mantiene estado de interfaz y coordina operaciones; no dibuja la vista ni ejecuta SQL/HTTP directamente.
4. **¿Qué hace un repository?** Aplica reglas de datos y conecta el resto de la app con Room o con el servicio remoto.
5. **¿Qué hace Retrofit?** Implementa las interfaces HTTP y convierte objetos Kotlin/JSON.
6. **¿Por qué hay un modelo API distinto de la entidad?** Los nombres/formato del servidor pueden diferir del modelo interno de la app.
7. **¿Qué diferencia POST de PUT y PATCH?** POST crea; PUT reemplaza; PATCH modifica parcialmente.
8. **¿Qué sucede al denegar la cámara?** Aparece un mensaje y se puede continuar sin foto.
9. **¿Qué sucede si no hay GPS disponible?** Se puede guardar el reporte sin coordenadas.
10. **¿Qué prueba conectada requiere dispositivo?** `connectedDebugAndroidTest`.
11. **¿Las pruebas unitarias prueban la cámara real?** No; permisos/cámara requieren comprobación instrumentada o manual.
12. **¿La foto se comparte con Xano?** No en esta versión; la app solo la muestra durante el formulario.
13. **¿El login usa Xano?** No; el login actual consulta cuentas locales en Room.
14. **¿Qué se debe proteger en Git?** Contraseñas y archivo de keystore de firma; `.gitignore` excluye esas credenciales.
15. **¿Qué haces si Xano responde 429?** Espero el límite temporal y pruebo de nuevo sin generar una ráfaga de solicitudes.

## 12. Plan de estudio práctico

1. Explica el guion oral sin leer y señala en el IDE `MisIncidenciasScreen`, `IncidenciaViewModel`, `IncidenciaRepository` y `XanoApiService`.
2. Recorre el flujo de crear una incidencia siguiendo el orden de capas y nombra el método HTTP que ocurre.
3. Practica en el emulador registro, login, reporte, filtro, detalle, cámara, GPS y cierre de sesión.
4. Ejecuta las pruebas locales y conectadas. Explica qué depende de Android y qué puede probarse sin dispositivo.
5. Abre README y Xano para relacionar documentación con la app; confirma el ícono, la firma release y que el APK se instala antes de enviarlo.
6. Para cada función que no está implementada, dilo claramente. No presentes la gestión simulada, la foto no sincronizada ni el login local como capacidades distintas.

## Archivos clave para estudiar

- `app/src/main/java/com/example/desarrolloappmoviles/ui/NuevaIncidenciaScreen.kt`
- `app/src/main/java/com/example/desarrolloappmoviles/ui/MisIncidenciasScreen.kt`
- `app/src/main/java/com/example/desarrolloappmoviles/ui/GPS.kt`
- `app/src/main/java/com/example/desarrolloappmoviles/viewmodel/IncidenciaViewModel.kt`
- `app/src/main/java/com/example/desarrolloappmoviles/repository/IncidenciaRepository.kt`
- `app/src/main/java/com/example/desarrolloappmoviles/repository/UsuarioRepository.kt`
- `app/src/main/java/com/example/desarrolloappmoviles/repository/XanoApiService.kt`
- `app/src/main/java/com/example/desarrolloappmoviles/repository/RetrofitInstance.kt`
- `app/src/main/java/com/example/desarrolloappmoviles/model/IncidenciaApiModel.kt`
- `app/src/main/java/com/example/desarrolloappmoviles/CuidAppApplication.kt`
