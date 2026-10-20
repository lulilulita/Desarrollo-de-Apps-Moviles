# CuidApp — avance básico de formularios

Login, registro y formulario de Nueva incidencia con Kotlin, Compose, Material 3 y ViewModels. Sin alias, cuentas guardadas, autenticación ni pantalla Inicio. Se conserva la configuración del proyecto y no se agregan dependencias.

## Probar

1. Ejecutar app desde Android Studio.
2. Enviar login vacío: muestra errores por campo.
3. Ingresar `vecino@ejemplo.com` y `123456`: tras dos segundos aparece la confirmación de validación. Pulsar Continuar abre Nueva incidencia. No requiere crear una cuenta antes.
4. Ir a registro. Probar correo inválido, contraseña corta y confirmación distinta.
5. Completar correctamente: muestra que el formulario es válido y que el guardado queda pendiente. El enlace permite regresar al login.

## Referencias verificadas del profesor

Repositorio: https://github.com/amaurelia/basic-app (rama master).

- `ui/Login.kt`: campos, botón y AlertDialog; no comprueba cuentas. Nuestro diálogo confirma validación y evita mostrar la contraseña.
- `repository/FormularioRepository.kt` y `viewmodel/UsuarioFormViewModel.kt`: validación de correo con `^[\w.-]+@[\w.-]+\.\w+$`, la misma que ya tenía CuidApp.
- `ui/UsuarioFormScreen.kt`: errores por campo mediante isError y supportingText, y confirmación al validar.
- `ui/Animacion.kt`: CircularProgressIndicator y delay de 2000 ms. CuidApp conserva rememberCoroutineScope para vincular la tarea a la pantalla.
- `viewmodel/FormularioViewModel.kt`: validaciones separadas de la interfaz.
- `repository/UsuarioRepository.kt` y `ui/AgregarUsuarios.kt`: lista de usuarios en memoria; no autentican ni persisten cuentas con contraseña. No se incorporan porque no hacen falta para este avance.

También se revisaron Inputs, Navegacion, Persistencia, InicioViewModel, InicioRepository, MensajesError y ButtonViewModel. No se copiaron módulos ajenos a registro/login ni se reemplazó la configuración de Gradle.

La contraseña de al menos seis caracteres y su confirmación son reglas previas de CuidApp, no reglas copiadas de los ejemplos revisados del profesor. Se mantienen para conservar el trabajo existente. Edad, términos y datos personales de otros formularios del profesor no se incorporan.

## Explicación oral

“La pantalla recoge los datos y muestra los errores. El ViewModel comprueba el formato del correo, el mínimo de contraseña y la confirmación del registro. El login simula una carga y muestra un diálogo. En este avance validamos el formulario; todavía no autenticamos cuentas ni guardamos usuarios.”

Archivos principales: MainActivity, LoginScreen, RegistroScreen, LoginViewModel y RegistroViewModel. NuevaIncidenciaScreen e IncidenciaViewModel implementan el formulario de prueba; los archivos base de persistencia e historial siguen pendientes.

No se hizo commit ni push. Usar datos ficticios. La siguiente etapa se definirá según lo visto en clase.

## Nuevo avance: Nueva incidencia

Para probar: login con `vecino@ejemplo.com` y `123456` → Continuar → Nueva incidencia. Enviar el formulario vacío para ver errores. Luego elegir Iluminación, escribir “Luminaria apagada en el sector ficticio A”, dejar prioridad Media y elegir Sector A. Pulsar Validar incidencia: aparece el resumen de prueba. Aceptar conserva el borrador; Nueva prueba limpia el formulario. Volver al login o Atrás regresa sin guardar. El borrador permanece en el ViewModel mientras vive la actividad y sobrevive a la rotación; no sobrevive a terminar el proceso.

Campos del mockup incluidos: categoría, descripción, prioridad y sector ficticio. No se agregaron cámara, GPS, mapa, cuentas, Room ni envío. El botón dice Validar incidencia para expresar lo que hace realmente.

`NuevaIncidenciaScreen.kt` dibuja el formulario y reutiliza un selector privado para las tres listas, siguiendo ExposedDropdownMenuBox de Inputs.kt del profesor. `IncidenciaViewModel.kt` mantiene campos y errores con mutableStateOf, siguiendo la sencillez de los ViewModels existentes. MainActivity conecta pantallas usando el mismo mecanismo de estado que ya tenía el proyecto, sin dependencias nuevas.

Reglas propias del caso: elegir una categoría y sector de las listas; descripción de 10 a 300 caracteres (sin contar espacios de los extremos para el mínimo); prioridad de la lista, Media por defecto. No son reglas copiadas del profesor: se aplica su patrón de validaciones por campo al formulario del mockup.

Defensa oral: “La pantalla muestra los controles. El ViewModel revisa cada campo y actualiza los errores. Compose vuelve a dibujar cuando cambia el estado. Si el formulario es válido aparece un diálogo de confirmación, como en el ejemplo del profesor. Este avance valida datos ficticios, todavía no guarda ni envía incidencias.”

Pruebas automáticas nuevas: formulario vacío, formulario válido y limpieza, límites de descripción y opciones inválidas. La siguiente etapa puede ser guardar las incidencias y mostrarlas en una lista cuando corresponda verlo en clase.
