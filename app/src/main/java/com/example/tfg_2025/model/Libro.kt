package com.example.tfg_2025.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "libros_table")
data class Libro(
    @PrimaryKey
    @SerializedName("id") val id: String,

    @SerializedName("titulo") val titulo: String?,
    @SerializedName("autor") val autor: String?,
    @SerializedName("urlPortada") val urlPortada: String?,


    var esFavorito: Boolean = false,
    var estaEnEstanteria: Boolean = false,
    var leyendo: Boolean = false,
    var leido: Boolean = false,
    var pendiente: Boolean = false
)