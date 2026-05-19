package com.example.tfg_2025.repository

import com.example.tfg_2025.api.ApiService
import com.example.tfg_2025.model.Libro
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class LibroRepository(private val apiService: ApiService) {

    suspend fun buscarLibros(query: String, limit: Int = 20): List<Libro> {
        return try {
            val queryLimpia = query.trim()
            val queryCodificada = URLEncoder.encode(queryLimpia, StandardCharsets.UTF_8.name())

            // Aquí pasamos el limit que recibe el repositorio
            val respuestaOpenLibrary = apiService.buscarLibros(queryCodificada, limit)

            respuestaOpenLibrary.items?.map { bookItem ->
                Libro(
                    id = bookItem.id,
                    titulo = bookItem.title ?: "Título desconocido",
                    autor = bookItem.authors?.firstOrNull() ?: "Autor desconocido",
                    urlPortada = bookItem.coverId?.let { "https://covers.openlibrary.org/b/id/$it-L.jpg" } ?: ""
                )
            } ?: emptyList()

        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}