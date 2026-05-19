package com.example.tfg_2025.ui.biblioteca

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BibliotecaFragment : Fragment(R.layout.fragment_biblioteca) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var libroAdapter: LibroBibliotecaAdapter
    private lateinit var tvVacio: TextView
    private lateinit var layoutContenido: LinearLayout
    private lateinit var imgDestacado: ImageView
    private lateinit var tvTituloDestacado: TextView
    private var ultimoClicTime: Long = 0

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
                    AppDatabase.getDatabase(requireContext()).libroDao().update(libro)
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
            esHorizontal = true  // ← nuevo
        )

        recyclerView.adapter = libroAdapter

        fab.setOnClickListener {
            if (System.currentTimeMillis() - ultimoClicTime < 500) return@setOnClickListener
            ultimoClicTime = System.currentTimeMillis()
            findNavController().navigate(R.id.action_bibliotecaFragment_to_perfilFragment)
        }

        val abrirListaFiltrada: (String) -> Unit = { tipo ->
            if (System.currentTimeMillis() - ultimoClicTime >= 600) {
                ultimoClicTime = System.currentTimeMillis()
                val bundle = Bundle().apply { putString("tipo_lista", tipo) }
                try {
                    findNavController().navigate(R.id.action_bibliotecaFragment_to_verListaFragment, bundle)
                } catch (e: Exception) {
                    findNavController().navigate(R.id.verListaFragment, bundle)
                }
            }
        }

        vista.findViewById<View>(R.id.btn_ver_leyendo)?.setOnClickListener { abrirListaFiltrada("leyendo") }
        vista.findViewById<View>(R.id.btn_ver_leidos)?.setOnClickListener { abrirListaFiltrada("leidos") }
        vista.findViewById<View>(R.id.btn_ver_pendientes)?.setOnClickListener { abrirListaFiltrada("pendientes") }
    }

    private fun cargarLibrosBiblioteca() {
        if (!isAdded || context == null) return

        val libroDao = AppDatabase.getDatabase(requireContext()).libroDao()

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            val listaLeyendo = libroDao.obtenerLibrosLeyendo()
            val listaCompleta = libroDao.obtenerLibrosEstanteria()
            val listaUltimosLeidos = libroDao.obtenerLibrosLeidos().take(5)
            val todosLibros = libroDao.obtenerTodosLosLibros()

            android.util.Log.d("BIBLIOTECA", "Leídos: ${listaUltimosLeidos.size}")
            android.util.Log.d("BIBLIOTECA", "En estantería: ${listaCompleta.size}")
            android.util.Log.d("BIBLIOTECA", "Total en BD: ${todosLibros.size}")
            todosLibros.forEach {
                android.util.Log.d("BIBLIOTECA", "${it.titulo} | leido=${it.leido} | estanteria=${it.estaEnEstanteria}")
            }

            withContext(Dispatchers.Main) {
                if (!isAdded) return@withContext

                if (listaCompleta.isEmpty()) {
                    tvVacio.visibility = View.VISIBLE
                    layoutContenido.visibility = View.GONE
                } else {
                    tvVacio.visibility = View.GONE
                    layoutContenido.visibility = View.VISIBLE

                    val libroDestacado = listaLeyendo.firstOrNull() ?: listaCompleta.first()
                    tvTituloDestacado.text = libroDestacado.titulo ?: "Sin título"

                    if (!libroDestacado.urlPortada.isNullOrEmpty()) {
                        Glide.with(requireContext())
                            .load(libroDestacado.urlPortada)
                            .placeholder(R.drawable.ic_menu_libro)
                            .error(R.drawable.ic_menu_libro)
                            .into(imgDestacado)
                    }

                    imgDestacado.setOnClickListener {
                        val id = libroDestacado.id ?: return@setOnClickListener
                        val bundle = Bundle().apply {
                            putString("id_libro", id)
                            putString("titulo_libro", libroDestacado.titulo ?: "")
                            putString("autor_libro", libroDestacado.autor ?: "")
                            putString("portada_libro", libroDestacado.urlPortada ?: "")
                        }
                        findNavController().navigate(R.id.action_bibliotecaFragment_to_detalleLibroFragment, bundle)
                    }

                    libroAdapter.updateList(listaUltimosLeidos.toMutableList())
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        cargarLibrosBiblioteca()
    }
}