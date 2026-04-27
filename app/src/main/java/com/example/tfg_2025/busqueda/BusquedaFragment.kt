package com.example.tfg_2025.ui.busqueda

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tfg_2025.R
import com.example.tfg_2025.ui.biblioteca.BibliotecaFragment // Reutilizamos el adaptador

class BusquedaFragment : Fragment(R.layout.fragment_busqueda) {

    override fun onViewCreated(vista: View, savedInstanceState: Bundle?) {
        super.onViewCreated(vista, savedInstanceState)

        val searchView = vista.findViewById<SearchView>(R.id.search_view)
        val rvResultados = vista.findViewById<RecyclerView>(R.id.rv_resultados)
        val layoutEspera = vista.findViewById<LinearLayout>(R.id.layout_espera)


        rvResultados.layoutManager = LinearLayoutManager(requireContext())

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) {
                    // Si se borra el texto, mostramos el mensaje de espera y ocultamos la lista
                    layoutEspera.visibility = View.VISIBLE
                    rvResultados.visibility = View.GONE
                } else {
                    // Si se escribe algo, ocultamos el mensaje y mostramos resultados
                    layoutEspera.visibility = View.GONE
                    rvResultados.visibility = View.VISIBLE

                }
                return true
            }
        })
    }
}