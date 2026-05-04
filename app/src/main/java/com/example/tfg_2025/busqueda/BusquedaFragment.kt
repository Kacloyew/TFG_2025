package com.example.tfg_2025.busqueda

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tfg_2025.adapter.LibroAdapter
import com.example.tfg_2025.databinding.FragmentBusquedaBinding
import com.example.tfg_2025.model.Libro

class BusquedaFragment : Fragment() {

    private var _binding: FragmentBusquedaBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBusquedaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Configurar RecyclerView
        binding.rvResultados.layoutManager = LinearLayoutManager(requireContext())

        // Configurar Buscador
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrEmpty()) {
                    ejecutarBusqueda(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean = true
        })
    }

    private fun ejecutarBusqueda(query: String) {
        // Por ahora, ocultamos el mensaje de espera y mostramos el RV
        binding.layoutEspera.visibility = View.GONE
        binding.rvResultados.visibility = View.VISIBLE
        
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}