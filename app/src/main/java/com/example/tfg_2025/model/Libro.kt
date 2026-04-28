package com.example.tfg_2025.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "libros_table") // Esto le dice a Room que cree una tabla
data class Libro(
    @PrimaryKey val id: String, // El ID que nos da la API
    val titulo: String,
    val autor: String,
    val descripcion: String,
    val urlPortada: String,
    val esFavorito: Boolean = false
)