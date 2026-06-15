package com.example.tfg_2025.repository

import com.example.tfg_2025.api.ApiService
import com.example.tfg_2025.data.LibroDao
import com.example.tfg_2025.model.Libro
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class LibroRepository(private val apiService: ApiService, private val libroDao: LibroDao) {

    suspend fun buscarLibros(query: String, limit: Int = 20): List<Libro> {
        return try {
            val queryCodificada = URLEncoder.encode(query.trim(), StandardCharsets.UTF_8.name())
            val respuesta = apiService.buscarLibros(queryCodificada, limit)

            respuesta.items?.map { bookItem ->
                val id = Libro.createSafeId(bookItem.id)
                val libroExistente = libroDao.obtenerLibroPorId(id)  // buscar en BD
                Libro(
                    id = id,
                    titulo = bookItem.title ?: "Título desconocido",
                    autor = bookItem.authors?.firstOrNull() ?: "Autor desconocido",
                    urlPortada = bookItem.coverId?.let { "https://covers.openlibrary.org/b/id/$it-L.jpg" } ?: "",
                    // Preservar estados guardados si existe
                    esFavorito = libroExistente?.esFavorito ?: false,
                    estaEnEstanteria = libroExistente?.estaEnEstanteria ?: false,
                    leyendo = libroExistente?.leyendo ?: false,
                    leido = libroExistente?.leido ?: false,
                    pendiente = libroExistente?.pendiente ?: false
                )
            } ?: emptyList()

        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}
