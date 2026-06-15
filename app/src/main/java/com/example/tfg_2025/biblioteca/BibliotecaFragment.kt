package com.example.tfg_2025.ui.biblioteca

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tfg_2025.R
import com.example.tfg_2025.adapter.LibroBibliotecaAdapter
import com.example.tfg_2025.data.AppDatabase
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BibliotecaFragment : Fragment(R.layout.fragment_biblioteca) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var libroAdapter: LibroBibliotecaAdapter
    private lateinit var tvVacio: TextView
    private lateinit var layoutContenido: LinearLayout
    private lateinit var imgDestacado: ImageView
    private lateinit var tvTituloDestacado: TextView

    override fun onViewCreated(vista: View, savedInstanceState: Bundle?) {
        super.onViewCreated(vista, savedInstanceState)

        val fab = vista.findViewById<FloatingActionButton>(R.id.fab_perfil)
        recyclerView = vista.findViewById(R.id.rv_biblioteca)
        tvVacio = vista.findViewById(R.id.tv_vacio)
        layoutContenido = vista.findViewById(R.id.layout_contenido_biblioteca)
        imgDestacado = vista.findViewById(R.id.img_libro_destacado)
        tvTituloDestacado = vista.findViewById(R.id.tv_titulo_destacado)

        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        libroAdapter = LibroBibliotecaAdapter(
            mutableListOf(),
            onFavoritoClick = { libro ->
                viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                    AppDatabase.getDatabase(requireContext(), userId).libroDao()
                        .actualizarEstadoFavorito(libro.id, !libro.esFavorito)
                    withContext(Dispatchers.Main) {
                        recyclerView.adapter?.notifyDataSetChanged()
                    }
                }
            },
            onItemClick = { libro ->
                val bundle = Bundle().apply {
                    putString("id_libro", libro.id)
                    putString("titulo_libro", libro.titulo)
                    putString("autor_libro", libro.autor)
                    putString("portada_libro", libro.urlPortada)
                }
                findNavController().navigate(R.id.action_bibliotecaFragment_to_detalleLibroFragment, bundle)
            },
            esHorizontal = true
        )

        recyclerView.adapter = libroAdapter
        cargarLibrosBiblioteca()

        fab.setOnClickListener {
            findNavController().navigate(R.id.action_bibliotecaFragment_to_perfilFragment)
        }

        val navegarConTipo: (String) -> Unit = { tipo ->
            val bundle = Bundle().apply { putString("tipo_lista", tipo) }
            findNavController().navigate(R.id.action_bibliotecaFragment_to_verListaFragment, bundle)
        }

        vista.findViewById<Button>(R.id.btn_ver_leyendo).setOnClickListener { navegarConTipo("leyendo") }
        vista.findViewById<Button>(R.id.btn_ver_leidos).setOnClickListener { navegarConTipo("leidos") }
        vista.findViewById<Button>(R.id.btn_ver_pendientes).setOnClickListener { navegarConTipo("pendientes") }
    }

    private fun cargarLibrosBiblioteca() {
        if (!isAdded) return
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        val libroDao = AppDatabase.getDatabase(requireContext(), userId).libroDao()

        viewLifecycleOwner.lifecycleScope.launch {

            val deferredLeyendo  = async(Dispatchers.IO) { libroDao.obtenerLibrosLeyendo() }
            val deferredCompleta = async(Dispatchers.IO) { libroDao.obtenerLibrosEstanteria() }
            val deferredLeidos   = async(Dispatchers.IO) { libroDao.obtenerLibrosLeidos() }

            val leyendo  = deferredLeyendo.await()
            val completa = deferredCompleta.await()
            val leidos   = deferredLeidos.await()

            if (completa.isEmpty()) {
                tvVacio.visibility = View.VISIBLE
                layoutContenido.visibility = View.GONE
            } else {
                tvVacio.visibility = View.GONE
                layoutContenido.visibility = View.VISIBLE

                val libroDestacado = leyendo.firstOrNull() ?: completa.first()
                tvTituloDestacado.text = libroDestacado.titulo

                imgDestacado.setOnClickListener {
                    val bundle = Bundle().apply {
                        putString("id_libro", libroDestacado.id)
                        putString("titulo_libro", libroDestacado.titulo)
                        putString("autor_libro", libroDestacado.autor)
                        putString("portada_libro", libroDestacado.urlPortada)
                    }
                    findNavController().navigate(R.id.action_bibliotecaFragment_to_detalleLibroFragment, bundle)
                }

                if (!libroDestacado.urlPortada.isNullOrEmpty()) {
                    Glide.with(requireContext())
                        .load(libroDestacado.urlPortada)
                        .centerCrop()
                        .into(imgDestacado)
                }

                libroAdapter.updateList(leidos.toMutableList())
            }
        }
    }

    override fun onResume() {
        super.onResume()
        cargarLibrosBiblioteca()
    }
}