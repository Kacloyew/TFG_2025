package com.example.tfg_2025.ui.busqueda

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tfg_2025.R
import com.example.tfg_2025.adapter.LibroBibliotecaAdapter
import com.example.tfg_2025.api.RetrofitClient
import com.example.tfg_2025.data.AppDatabase
import com.example.tfg_2025.repository.LibroRepository
import com.example.tfg_2025.viewmodel.BusquedaViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BusquedaFragment : Fragment(R.layout.fragment_busqueda) {

    private lateinit var viewModel: BusquedaViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Configuración del ViewModel
        val repository = LibroRepository(RetrofitClient.instance)
        val libroDao = AppDatabase.getDatabase(requireContext()).libroDao()

        val factory = object : ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return BusquedaViewModel(repository, libroDao) as T
            }
        }
        viewModel = ViewModelProvider(this, factory)[BusquedaViewModel::class.java]

        // 2. Configuración de Vistas
        val searchView = view.findViewById<SearchView>(R.id.search_view)
        val recyclerView = view.findViewById<RecyclerView>(R.id.rv_resultados)
        val layoutEspera = view.findViewById<View>(R.id.layout_espera)

        // Inicialización con 3 parámetros: Lista, Favorito, Navegación
        val adapter = LibroBibliotecaAdapter(
            mutableListOf(),
            onFavoritoClick = { libro ->
                viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                    AppDatabase.getDatabase(requireContext()).libroDao().update(libro)
                }
            },
            onItemClick = { libroSeleccionado ->
                // Navegación usando la acción que definiste en el XML
                val bundle = Bundle().apply {
                    putString("id_libro", libroSeleccionado.id)
                    putString("titulo_libro", libroSeleccionado.titulo)
                    putString("autor_libro", libroSeleccionado.autor)
                    putString("portada_libro", libroSeleccionado.urlPortada)
                }
                findNavController().navigate(R.id.action_busquedaFragment_to_detalleLibroFragment, bundle)
            }
        )

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // 3. Observar resultados
        viewModel.libros.observe(viewLifecycleOwner) { libros ->
            layoutEspera.visibility = if (libros.isEmpty()) View.VISIBLE else View.GONE
            recyclerView.visibility = if (libros.isNotEmpty()) View.VISIBLE else View.GONE
            adapter.updateList(libros.toMutableList())
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrBlank()) {
                    viewModel.buscarLibros(query)
                    searchView.clearFocus()
                }
                return true
            }
            override fun onQueryTextChange(newText: String?): Boolean = false
        })
    }
}