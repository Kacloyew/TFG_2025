package com.example.tfg_2025.ui.biblioteca

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tfg_2025.R
import com.example.tfg_2025.adapter.LibroBibliotecaAdapter
import com.example.tfg_2025.data.AppDatabase
import com.example.tfg_2025.model.Libro
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class VerListaFragment : Fragment(R.layout.fragment_ver_lista) {

    private lateinit var adapter: LibroBibliotecaAdapter
    private var tipoLista: String = "leidos"

    override fun onViewCreated(vista: View, savedInstanceState: Bundle?) {
        super.onViewCreated(vista, savedInstanceState)

        val tvTitulo = vista.findViewById<TextView>(R.id.tv_titulo_lista)
        val btnAtras = vista.findViewById<Button>(R.id.btn_lista_atras)
        val rvLista = vista.findViewById<RecyclerView>(R.id.rv_lista_completa)

        tipoLista = (arguments?.getString("tipo_lista") ?: "leidos").lowercase().trim()

        tvTitulo.text = when (tipoLista) {
            "leyendo"    -> "Libros Leyendo"
            "leidos"     -> "Libros Leídos"
            "pendientes" -> "Libros Pendientes"
            else         -> "Mis Libros"
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : androidx.activity.OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigate(R.id.nav_biblioteca)
                }
            }
        )

        btnAtras.setOnClickListener {
            btnAtras.isEnabled = false
            findNavController().navigate(R.id.nav_biblioteca)
        }

        rvLista.layoutManager = LinearLayoutManager(requireContext())

        adapter = LibroBibliotecaAdapter(
            mutableListOf(),
            onFavoritoClick = { libro ->
                viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                    AppDatabase.getDatabase(requireContext(), userId).libroDao().insertarLibro(libro)
                }
            },
            onItemClick = { libroSeleccionado ->
                val bundle = Bundle().apply {
                    putString("id_libro", libroSeleccionado.id)
                    putString("titulo_libro", libroSeleccionado.titulo)
                    putString("autor_libro", libroSeleccionado.autor)
                    putString("portada_libro", libroSeleccionado.urlPortada)
                }
                findNavController().navigate(
                    R.id.action_verListaFragment_to_detalleLibroFragment, bundle
                )
            }
        )

        rvLista.adapter = adapter
        cargarLista()
    }

    private fun cargarLista() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        val dao = AppDatabase.getDatabase(requireContext(), userId).libroDao()

        val flujo: Flow<List<Libro>> = when (tipoLista) {
            "leyendo"    -> dao.obtenerLibrosLeyendoFlow()
            "leidos"     -> dao.obtenerLibrosLeidosFlow()
            "pendientes" -> dao.obtenerLibrosPendientesFlow()
            else         -> dao.obtenerLibrosEstanteriaFlow()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            flujo.collect { lista ->
                adapter.updateList(lista.toMutableList())
            }
        }
    }
}