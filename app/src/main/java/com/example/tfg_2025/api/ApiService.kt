package com.example.tfg_2025.api

import com.example.tfg_2025.model.LibroRespuesta
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("search.json")
    suspend fun buscarLibros(
        @Query("q") query: String,
        @Query("limit") limit: Int,
        //@Query("lang") lang: String = "spa"
    ): LibroRespuesta
}