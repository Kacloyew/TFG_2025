package com.example.tfg_2025.ui.favoritos

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tfg_2025.R
import com.example.tfg_2025.adapter.LibroBibliotecaAdapter
import com.example.tfg_2025.data.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FavoritosFragment : Fragment(R.layout.fragment_favoritos) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var libroAdapter: LibroBibliotecaAdapter

    override fun onViewCreated(vista: View, savedInstanceState: Bundle?) {
        super.onViewCreated(vista, savedInstanceState)

        recyclerView = vista.findViewById(R.id.rv_favoritos)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        // IMPORTANTE: Aquí pasas los TRES parámetros que requiere el nuevo Adaptador
        libroAdapter = LibroBibliotecaAdapter(
            mutableListOf(),
            onFavoritoClick = { libro ->
                viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                    AppDatabase.getDatabase(requireContext()).libroDao().update(libro)
                }
            },
            onItemClick = { libro ->
                // Navegación al detalle
                val bundle = Bundle().apply {
                    putString("id_libro", libro.id)
                    putString("titulo_libro", libro.titulo)
                    putString("autor_libro", libro.autor)
                    putString("portada_libro", libro.urlPortada)
                }
                // Asegúrate de que esta acción existe en nav_graph.xml
                findNavController().navigate(R.id.action_favoritosFragment_to_detalleLibroFragment, bundle)
            }
        )

        recyclerView.adapter = libroAdapter
        cargarLibrosFavoritos()
    }

    private fun cargarLibrosFavoritos() {
        val database = AppDatabase.getDatabase(requireContext())
        val libroDao = database.libroDao()

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            val listaFavoritos = libroDao.obtenerLibrosFavoritos()

            withContext(Dispatchers.Main) {
                if (!isAdded || context == null) return@withContext
                libroAdapter.updateList(listaFavoritos.toMutableList())
            }
        }
    }

    override fun onResume() {
        super.onResume()
        cargarLibrosFavoritos()
    }
}