package com.example.tfg_2025.data

import androidx.room.*
import com.example.tfg_2025.model.Libro

@Dao
interface LibroDao {

    @Query("SELECT * FROM libros_table")
    fun obtenerTodosLosLibros(): List<Libro>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarLibro(libro: Libro)

    @Delete
    suspend fun eliminarLibro(libro: Libro)

    @Query("SELECT * FROM libros_table WHERE id = :idLibro")
    suspend fun obtenerLibroPorId(idLibro: String): Libro?
}