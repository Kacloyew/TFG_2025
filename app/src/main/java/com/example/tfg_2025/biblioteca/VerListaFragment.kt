package com.example.tfg_2025.ui.biblioteca

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tfg_2025.R
import com.example.tfg_2025.adapter.LibroBibliotecaAdapter
import com.example.tfg_2025.data.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class VerListaFragment : Fragment(R.layout.fragment_ver_lista) {

    private lateinit var adapter: LibroBibliotecaAdapter
    private var tipoLista: String = "leidos"

    override fun onViewCreated(vista: View, savedInstanceState: Bundle?) {
        super.onViewCreated(vista, savedInstanceState)

        // Recuperar tipo de lista
        tipoLista = (arguments?.getString("tipo_lista") ?: "leidos").lowercase().trim()

        // UI
        val tvTitulo = vista.findViewById<TextView>(R.id.tv_titulo_lista)
        val btnAtras = vista.findViewById<Button>(R.id.btn_lista_atras)
        val rvLista = vista.findViewById<RecyclerView>(R.id.rv_lista_completa)

        tvTitulo.text = when (tipoLista) {
            "leyendo" -> "Libros Leyendo"
            "leidos" -> "Libros Leídos"
            "pendientes" -> "Libros Pendientes"
            else -> "Mis Libros"
        }

        btnAtras.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // Configuración del adaptador con los 3 parámetros requeridos
        rvLista.layoutManager = LinearLayoutManager(requireContext())

        adapter = LibroBibliotecaAdapter(
            mutableListOf(),
            onFavoritoClick = { libro ->
                viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                    AppDatabase.getDatabase(requireContext()).libroDao().update(libro)
                }
            },
            onItemClick = { libroSeleccionado ->
                // Navegación al detalle
                val bundle = Bundle().apply {
                    putString("id_libro", libroSeleccionado.id)
                    putString("titulo_libro", libroSeleccionado.titulo)
                    putString("autor_libro", libroSeleccionado.autor)
                    putString("portada_libro", libroSeleccionado.urlPortada)
                }
                findNavController().navigate(R.id.action_verListaFragment_to_detalleLibroFragment, bundle)
            }
        )

        rvLista.adapter = adapter
    }

    private fun cargarLista() {
        if (!isAdded || context == null) return

        val libroDao = AppDatabase.getDatabase(requireContext()).libroDao()

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            val librosFiltrados = when (tipoLista) {
                "leyendo" -> libroDao.obtenerLibrosLeyendo()
                "leidos" -> libroDao.obtenerLibrosLeidos()
                "pendientes" -> libroDao.obtenerLibrosPendientes()
                else -> emptyList()
            }

            withContext(Dispatchers.Main) {
                if (isAdded && context != null) {
                    adapter.updateList(librosFiltrados.toMutableList())
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        cargarLista()
    }
}