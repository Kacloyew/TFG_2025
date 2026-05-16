package com.example.tfg_2025.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tfg_2025.model.Libro
import com.example.tfg_2025.model.LibroRespuesta
import com.example.tfg_2025.repository.LibroRepository
import kotlinx.coroutines.launch

class BusquedaViewModel(private val repository: LibroRepository) : ViewModel() {


    private val _libros = MutableLiveData<List<LibroRespuesta.BookItem>>()
    val libros: LiveData<List<LibroRespuesta.BookItem>> get() = _libros


    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    fun buscarLibros(query: String) {
        viewModelScope.launch {
            try {
                val respuesta = repository.buscarLibros(query)
                _libros.postValue(respuesta.items ?: emptyList())
                _error.postValue(null) // Limpiamos errores previos
            } catch (e: Exception) {
                _error.postValue(e.message)
            }
        }
    }
}