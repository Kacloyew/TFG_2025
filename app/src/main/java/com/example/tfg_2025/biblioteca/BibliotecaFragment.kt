package com.example.tfg_2025.ui.biblioteca

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.tfg_2025.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class BibliotecaFragment : Fragment(R.layout.fragment_biblioteca) {

    override fun onViewCreated(vista: View, savedInstanceState: Bundle?) {
        super.onViewCreated(vista, savedInstanceState)

        val fab = vista.findViewById<FloatingActionButton>(R.id.fab_perfil)

        // Por ahora, solo configuramos el botón para ir al perfil
        fab.setOnClickListener {
            findNavController().navigate(R.id.action_bibliotecaFragment_to_perfilFragment)
        }
    }
}