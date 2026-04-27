package com.example.tfg_2025.ui.perfil

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.tfg_2025.R

class PerfilFragment : Fragment(R.layout.fragment_perfil) {

    override fun onViewCreated(vista: View, savedInstanceState: Bundle?) {
        super.onViewCreated(vista, savedInstanceState)

        val botonSalir = vista.findViewById<Button>(R.id.boton_cerrar_sesion)
        val nombreUsuario = vista.findViewById<TextView>(R.id.nombre_usuario)
        val botonEditar = vista.findViewById<Button>(R.id.boton_editar_perfil)

        // Nombre de prueba
        nombreUsuario.text = "Usuario"

        // Acción: Cerrar Sesión
        botonSalir.setOnClickListener {
            Toast.makeText(requireContext(), "Cerrando sesión...", Toast.LENGTH_SHORT).show()
            // Usamos la acción que definimos en el grafo
            findNavController().navigate(R.id.action_perfilFragment_to_loginFragment)
        }

        // Acción: Ir a Editar Perfil
        botonEditar?.setOnClickListener {
            findNavController().navigate(R.id.action_perfilFragment_to_editarPerfilFragment)
        }
    }
}