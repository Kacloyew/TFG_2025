package com.example.tfg_2025.ui.autenticacion

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.tfg_2025.R

class RegistroFragment : Fragment(R.layout.fragment_registro) {

    override fun onViewCreated(vista: View, savedInstanceState: Bundle?) {
        super.onViewCreated(vista, savedInstanceState)

        val botonRegistrar = vista.findViewById<Button>(R.id.boton_registrar)
        val enlaceLogin = vista.findViewById<TextView>(R.id.texto_volver_login)


        botonRegistrar.setOnClickListener {
            findNavController().navigate(R.id.action_registroFragment_to_bibliotecaFragment)
        }


        enlaceLogin.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}