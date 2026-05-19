package com.example.tfg_2025.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tfg_2025.data.LibroDao
import com.example.tfg_2025.model.Libro
import com.example.tfg_2025.repository.LibroRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BusquedaViewModel(
    private val repository: LibroRepository,
    private val libroDao: LibroDao // Añadimos el DAO aquí
) : ViewModel() {

    private val _libros = MutableLiveData<List<Libro>>()
    val libros: LiveData<List<Libro>> get() = _libros

    fun buscarLibros(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val resultadosApi = repository.buscarLibros(query)

            // Sincronizamos cada libro de la API con la base de datos
            val listaSincronizada = resultadosApi.map { libroApi ->
                val libroLocal = libroDao.obtenerLibroPorId(libroApi.id)
                // Si existe en local, usamos ese objeto
                // Si no existe, devolvemos el original
                libroLocal ?: libroApi
            }

            _libros.postValue(listaSincronizada)
        }
    }
}