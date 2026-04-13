package com.example.tfg_2025.model

/**
 * Esta clase es el "molde" para los libros.
 */
data class Libro(
    val id: String,
    val titulo: String,
    val autores: List<String>?,
    val descripcion: String?,
    val urlPortada: String?
)