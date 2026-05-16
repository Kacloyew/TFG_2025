package com.example.tfg_2025.ui.busqueda

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tfg_2025.adapter.LibroAdapter
import com.example.tfg_2025.databinding.FragmentBusquedaBinding
import com.example.tfg_2025.viewmodel.BusquedaViewModel
import com.example.tfg_2025.repository.LibroRepository
import com.example.tfg_2025.api.RetrofitClient

class BusquedaFragment : Fragment() {

    private var _binding: FragmentBusquedaBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: BusquedaViewModel
    private lateinit var libroAdapter: LibroAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBusquedaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Inicializar Adapter con lista vacía
        libroAdapter = LibroAdapter(mutableListOf())
        binding.rvResultados.layoutManager = LinearLayoutManager(requireContext())
        binding.rvResultados.adapter = libroAdapter

        // 2. ViewModel
        val apiService = RetrofitClient.instance
        val repository = LibroRepository(apiService)
        viewModel = BusquedaViewModel(repository)

        // 3. Observador
        viewModel.libros.observe(viewLifecycleOwner) { listaLibros ->
            if (listaLibros != null) {
                // Actualizamos los datos
                libroAdapter.updateList(listaLibros)

                // Forzamos visibilidad
                binding.layoutEspera.visibility = View.GONE
                binding.rvResultados.visibility = View.VISIBLE

                // Scroll al inicio por si había una búsqueda previa
                binding.rvResultados.scrollToPosition(0)
            }
        }

        // 4. Configurar Buscador (A prueba de errores)
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrEmpty()) {
                    viewModel.buscarLibros(query)


                    binding.searchView.clearFocus()

                    binding.layoutEspera.visibility = View.VISIBLE
                    binding.rvResultados.visibility = View.GONE
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {

                if (newText.isNullOrEmpty()) {
                    libroAdapter.updateList(emptyList())
                }
                return true
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}