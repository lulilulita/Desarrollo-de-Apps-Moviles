# Avance ajustado al material de clase

Se retiraron la autenticación contra cuentas, SharedPreferences, hash, sal, rechazo de cuentas inexistentes o duplicadas y las pruebas de ese repositorio. Se restauraron los ViewModels sencillos y las reglas originales. Se conserva la carga simulada de dos segundos y se presenta una confirmación con AlertDialog, siguiendo los ejemplos Login y Animacion del profesor.

El registro solo valida; no crea ni guarda cuentas. El login acepta cualquier correo con formato válido y contraseña de seis caracteres o más, sin requerir registro previo. No hay Inicio ni alias.

La comprobación de contraseña mínima y confirmación ya existía en CuidApp. No se atribuye al profesor. Ver README para referencias verificadas y prueba manual.

No hay emulador/dispositivo conectado: queda pendiente la prueba visual. No se hizo commit ni push; cambios de .idea conservados.

## Avance siguiente: formulario Nueva incidencia

Se agregó NuevaIncidenciaScreen y se implementó IncidenciaViewModel: categoría, descripción, prioridad y sector ficticio. Login simulado → Continuar abre el formulario. Incluye errores por campo, resumen en diálogo y limpieza opcional. No guarda ni envía datos. No incorpora dependencias nuevas ni implementa foto, GPS o mapa.

Comprobaciones finales ejecutadas: assembleDebug correcto; testDebugUnitTest correcto (4 pruebas nuevas de incidencias y 1 de ejemplo, sin fallos); lintDebug correcto sin errores; git diff --check correcto. Se mantiene el aviso de compatibilidad de AGP con compileSdk 37.1 de la configuración original. No se verificó visualmente en emulador o dispositivo.
