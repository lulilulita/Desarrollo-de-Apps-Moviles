package com.example.desarrolloappmoviles.model

data class IncidenciaApiModel(
    val id: Long = 0,
    val categoria: String = "",
    val descripcion: String = "",
    val prioridad: String = "",
    val sector: String = "",
    val estado: String = "Ingresada",
    val responsable: String = "Sin asignar",
    val fecha: Long = 0,
    val created_at: Long = 0,
    val correo: String = "",
    val coordenadas: CoordenadasApiModel? = null,
    val historialAcciones: String = ""
) {
    fun aEntidad() = IncidenciaEntity(
        id = id, categoria = categoria, descripcion = descripcion, prioridad = prioridad,
        sector = sector, estado = estado, responsable = responsable,
        fechaRegistro = fecha.takeIf { it > 0 } ?: created_at.takeIf { it > 0 } ?: System.currentTimeMillis(),
        latitud = coordenadas?.latitud, longitud = coordenadas?.longitud,
        correoUsuario = correo.takeIf { it.isNotBlank() },
        historialAcciones = historialAcciones.takeIf { it.isNotBlank() }
    )

    companion object {
        fun desde(incidencia: IncidenciaEntity) = IncidenciaApiModel(
            id = incidencia.id, categoria = incidencia.categoria,
            descripcion = incidencia.descripcion, prioridad = incidencia.prioridad,
            sector = incidencia.sector, estado = incidencia.estado,
            responsable = incidencia.responsable, fecha = incidencia.fechaRegistro,
            correo = incidencia.correoUsuario.orEmpty(),
            coordenadas = CoordenadasApiModel(incidencia.latitud, incidencia.longitud),
            historialAcciones = incidencia.historialAcciones.orEmpty()
        )
    }
}

data class CoordenadasApiModel(val latitud: Double? = null, val longitud: Double? = null)
