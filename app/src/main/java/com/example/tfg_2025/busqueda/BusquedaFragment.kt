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
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BusquedaFragment : Fragment(R.layout.fragment_busqueda) {

    private lateinit var viewModel: BusquedaViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        val libroDao = AppDatabase.getDatabase(requireContext(), userId).libroDao()
        val repository = LibroRepository(RetrofitClient.instance, libroDao)

        val factory = object : ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return BusquedaViewModel(repository, libroDao) as T
            }

        }
        viewModel = ViewModelProvider(this, factory)[BusquedaViewModel::class.java]

        val searchView = view.findViewById<SearchView>(R.id.search_view)
        val recyclerView = view.findViewById<RecyclerView>(R.id.rv_resultados)
        val layoutEspera = view.findViewById<View>(R.id.layout_espera)

        val adapter = LibroBibliotecaAdapter(
            mutableListOf(),
            onFavoritoClick = { libro ->
                viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                    val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                    AppDatabase.getDatabase(requireContext(), uid).libroDao().insertarLibro(libro)
                }
            },
            onItemClick = { libroSeleccionado ->
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

        viewModel.libros.observe(viewLifecycleOwner) { libros ->
            if (libros != null) {
                layoutEspera.visibility = if (libros.isEmpty()) View.VISIBLE else View.GONE
                recyclerView.visibility = if (libros.isNotEmpty()) View.VISIBLE else View.GONE
                adapter.updateList(libros.toMutableList())
            }
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