package com.example.tfg_2025.ui.perfil

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.tfg_2025.R

class PerfilFragment : Fragment(R.layout.fragment_perfil) {

    override fun onViewCreated(vista: View, savedInstanceState: Bundle?) {
        super.onViewCreated(vista, savedInstanceState)


        val botonSalir = vista.findViewById<Button>(R.id.boton_cerrar_sesion)
        val nombreUsuario = vista.findViewById<TextView>(R.id.nombre_usuario)

        //nombre de prueba hasta poner la base de datos
        nombreUsuario.text = "Usuario de Prueba"

        botonSalir.setOnClickListener {
            findNavController().navigate(R.id.action_perfilFragment_to_loginFragment)
        }
    }
}