package com.example.apilazycolumn.api

import com.example.apilazycolumn.modelo.RespuestaPersonajes
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("character")
    suspend fun obtenerPersonajes(@Query("name") nombre: String?): RespuestaPersonajes
}
