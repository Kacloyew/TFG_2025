package com.example.tfg_2025.repository

import com.example.tfg_2025.api.ApiService
import com.example.tfg_2025.model.LibroRespuesta


class LibroRepository(private val apiService: ApiService) {

    // Esta función encapsula la llamada a la API
    suspend fun buscarLibros(query: String): LibroRespuesta {
        return apiService.buscarLibros(query)
    }
}