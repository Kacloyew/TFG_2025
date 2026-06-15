package com.example.tfg_2025.data

import androidx.room.*
import com.example.tfg_2025.model.Libro
import kotlinx.coroutines.flow.Flow

@Dao
interface LibroDao {

    @Query("SELECT * FROM libros_table WHERE estaEnEstanteria = 1")
    suspend fun obtenerLibrosEstanteria(): List<Libro>

    @Query("SELECT * FROM libros_table")
    suspend fun obtenerTodosLosLibros(): List<Libro>

    @Query("SELECT * FROM libros_table WHERE esFavorito = 1")
    fun obtenerLibrosFavoritos(): Flow<List<Libro>> //

    @Query("SELECT * FROM libros_table WHERE id = :id LIMIT 1")
    suspend fun obtenerLibroPorId(id: String): Libro?

    @Query("SELECT * FROM libros_table WHERE leyendo = 1 AND estaEnEstanteria = 1")
    suspend fun obtenerLibrosLeyendo(): List<Libro>

    @Query("SELECT * FROM libros_table WHERE leido = 1 AND estaEnEstanteria = 1")
    suspend fun obtenerLibrosLeidos(): List<Libro>

    @Query("SELECT * FROM libros_table WHERE pendiente = 1 AND estaEnEstanteria = 1")
    suspend fun obtenerLibrosPendientes(): List<Libro>

    @Query("UPDATE libros_table SET esFavorito = :estado WHERE id = :id")
    suspend fun actualizarEstadoFavorito(id: String, estado: Boolean)

    @Query("SELECT * FROM libros_table WHERE leyendo = 1 AND estaEnEstanteria = 1")
    fun obtenerLibrosLeyendoFlow(): Flow<List<Libro>>

    @Query("SELECT * FROM libros_table WHERE leido = 1 AND estaEnEstanteria = 1")
    fun obtenerLibrosLeidosFlow(): Flow<List<Libro>>

    @Query("SELECT * FROM libros_table WHERE pendiente = 1 AND estaEnEstanteria = 1")
    fun obtenerLibrosPendientesFlow(): Flow<List<Libro>>

    @Query("SELECT * FROM libros_table WHERE estaEnEstanteria = 1")
    fun obtenerLibrosEstanteriaFlow(): Flow<List<Libro>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarLibro(libro: Libro)

    @Delete
    suspend fun eliminarLibro(libro: Libro)

    @Update
    suspend fun update(libro: Libro)
}