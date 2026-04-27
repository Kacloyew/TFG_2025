package com.example.tfg_2025.ui.biblioteca

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.tfg_2025.R

class DetalleLibroFragment : Fragment(R.layout.fragment_detalle_libro) {

    override fun onViewCreated(vista: View, savedInstanceState: Bundle?) {
        super.onViewCreated(vista, savedInstanceState)

        val botonAñadir = vista.findViewById<Button>(R.id.btn_añadir_biblioteca)

        botonAñadir.setOnClickListener {
            Toast.makeText(requireContext(), "Libro añadido (simulación)", Toast.LENGTH_SHORT).show()
        }
    }
}