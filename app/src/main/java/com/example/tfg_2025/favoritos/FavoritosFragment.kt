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
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FavoritosFragment : Fragment(R.layout.fragment_favoritos) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var libroAdapter: LibroBibliotecaAdapter

    override fun onViewCreated(vista: View, savedInstanceState: Bundle?) {
        super.onViewCreated(vista, savedInstanceState)

        recyclerView = vista.findViewById(R.id.rv_favoritos)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        libroAdapter = LibroBibliotecaAdapter(
            mutableListOf(),
            onFavoritoClick = { libro ->
                viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                    AppDatabase.getDatabase(requireContext(), userId).libroDao().insertarLibro(libro)
                }
            },
            onItemClick = { libro ->
                val bundle = Bundle().apply {
                    putString("id_libro", libro.id)
                    putString("titulo_libro", libro.titulo)
                    putString("autor_libro", libro.autor)
                    putString("portada_libro", libro.urlPortada)
                }
                findNavController().navigate(
                    R.id.action_favoritosFragment_to_detalleLibroFragment,
                    bundle
                )
            }
        )

        recyclerView.adapter = libroAdapter
        cargarLibrosFavoritos()
    }

    private fun cargarLibrosFavoritos() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        val libroDao = AppDatabase.getDatabase(requireContext(), userId).libroDao()

        viewLifecycleOwner.lifecycleScope.launch {
            libroDao.obtenerLibrosFavoritos().collect { listaFavoritos ->
                libroAdapter.updateList(listaFavoritos.toMutableList())
            }
        }
    }


}