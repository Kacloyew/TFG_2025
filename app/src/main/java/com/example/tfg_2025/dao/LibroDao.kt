package com.example.tfg_2025.data

import androidx.room.*
import com.example.tfg_2025.model.Libro

@Dao
interface LibroDao {

    @Query("SELECT * FROM libros_table WHERE estaEnEstanteria = 1")
    suspend fun obtenerLibrosEstanteria(): List<Libro>

    @Query("SELECT * FROM libros_table")
    suspend fun obtenerTodosLosLibros(): List<Libro>

    @Query("SELECT * FROM libros_table WHERE esFavorito = 1")
    suspend fun obtenerLibrosFavoritos(): List<Libro>

    @Query("SELECT * FROM libros_table WHERE id = :id LIMIT 1")
    suspend fun obtenerLibroPorId(id: String): Libro?

    @Query("SELECT * FROM libros_table WHERE leyendo = 1 AND estaEnEstanteria = 1")
    suspend fun obtenerLibrosLeyendo(): List<Libro>

    @Query("SELECT * FROM libros_table WHERE leido = 1 AND estaEnEstanteria = 1")
    suspend fun obtenerLibrosLeidos(): List<Libro>

    @Query("SELECT * FROM libros_table WHERE pendiente = 1 AND estaEnEstanteria = 1")
    suspend fun obtenerLibrosPendientes(): List<Libro>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarLibro(libro: Libro)

    @Delete
    suspend fun eliminarLibro(libro: Libro)

    @Update
    suspend fun update(libro: Libro)
}