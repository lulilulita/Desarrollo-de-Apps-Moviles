package com.example.desarrolloappmoviles.repository

import com.example.desarrolloappmoviles.model.IncidenciaApiModel
import retrofit2.Response
import retrofit2.http.*

interface XanoApiService {
    @GET("incidencias") suspend fun listar(): List<IncidenciaApiModel>
    @GET("incidencias/{incidencias_id}") suspend fun obtener(@Path("incidencias_id") id: Long): IncidenciaApiModel
    @GET("incidencias-por-estado") suspend fun filtrar(@Query("estado") estado: String): List<IncidenciaApiModel>
    @POST("incidencias") suspend fun crear(@Body incidencia: IncidenciaApiModel): IncidenciaApiModel
    @PUT("incidencias/{incidencias_id}") suspend fun reemplazar(@Path("incidencias_id") id: Long, @Body incidencia: IncidenciaApiModel): IncidenciaApiModel
    @PATCH("incidencias/{incidencias_id}") suspend fun actualizar(@Path("incidencias_id") id: Long, @Body incidencia: IncidenciaApiModel): IncidenciaApiModel
    @DELETE("incidencias/{incidencias_id}") suspend fun eliminar(@Path("incidencias_id") id: Long): Response<Unit>
}
