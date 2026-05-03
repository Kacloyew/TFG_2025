package com.example.tfg_2025.ui.busqueda

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tfg_2025.R
import com.example.tfg_2025.adapter.LibroAdapter
import com.example.tfg_2025.api.RetrofitClient
import com.example.tfg_2025.databinding.FragmentBusquedaBinding
import kotlinx.coroutines.launch

class BusquedaFragment : Fragment(R.layout.fragment_busqueda) {

    private var _binding: FragmentBusquedaBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Esta línea es la que vincula el XML con el código
        _binding = FragmentBusquedaBinding.bind(view)

        // 1. Configurar RecyclerView
        binding.rvResultados.layoutManager = LinearLayoutManager(requireContext())

        // 2. Configurar el buscador
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrEmpty()) {
                    buscarLibros(query)
                }
                return true
            }
            override fun onQueryTextChange(newText: String?): Boolean = true
        })
    }

    private fun buscarLibros(query: String) {
        // Ocultar layout de espera y mostrar lista
        binding.layoutEspera.visibility = View.GONE
        binding.rvResultados.visibility = View.VISIBLE

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                // Llamada a la API
                val respuesta = RetrofitClient.instance.buscarLibros(query)
                val lista = respuesta.items ?: emptyList()

                if (lista.isNotEmpty()) {
                    binding.rvResultados.adapter = LibroAdapter(lista)
                } else {
                    Toast.makeText(requireContext(), "No se han encontrado resultados", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                binding.layoutEspera.visibility = View.VISIBLE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}