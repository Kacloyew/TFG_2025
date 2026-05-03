package com.example.tfg_2025.ui.busqueda

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tfg_2025.databinding.FragmentBusquedaBinding
import com.example.tfg_2025.adapter.LibroAdapter
import com.example.tfg_2025.api.RetrofitClient
import com.example.tfg_2025.repository.LibroRepository
import com.example.tfg_2025.viewmodel.BusquedaViewModel

class BusquedaFragment : Fragment() {

    // View Binding
    private var _binding: FragmentBusquedaBinding? = null
    private val binding get() = _binding!!

    // Inicialización del ViewModel y Repository
    private val viewModel: BusquedaViewModel by lazy {
        val repository = LibroRepository(RetrofitClient.instance)
        BusquedaViewModel(repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBusquedaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupSearchView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        binding.rvResultados.layoutManager = LinearLayoutManager(requireContext())

    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrEmpty()) {

                    binding.layoutEspera.visibility = View.GONE
                    binding.rvResultados.visibility = View.VISIBLE
                    viewModel.buscarLibros(query)


                    binding.searchView.clearFocus()
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean = true
        })
    }

    private fun observeViewModel() {
        // Observamos la lista de libros
        viewModel.libros.observe(viewLifecycleOwner) { listaLibros ->
            if (listaLibros.isNotEmpty()) {
                binding.rvResultados.adapter = LibroAdapter(listaLibros)
            } else {
                Toast.makeText(requireContext(), "No se encontraron resultados", Toast.LENGTH_SHORT).show()
            }
        }

        // Observamos posibles errores de red
        viewModel.error.observe(viewLifecycleOwner) { mensajeError ->
            mensajeError?.let {
                Toast.makeText(requireContext(), "Error: $it", Toast.LENGTH_SHORT).show()
                binding.layoutEspera.visibility = View.VISIBLE
                binding.rvResultados.visibility = View.GONE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}