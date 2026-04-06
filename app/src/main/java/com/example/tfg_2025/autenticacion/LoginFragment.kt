package com.example.tfg_2025.ui.autenticacion

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.tfg_2025.R

class LoginFragment : Fragment(R.layout.fragment_login) {

    override fun onViewCreated(vista: View, savedInstanceState: Bundle?) {
        super.onViewCreated(vista, savedInstanceState)


        val botonIngresar = vista.findViewById<Button>(R.id.boton_ingresar)

        botonIngresar.setOnClickListener {

            findNavController().navigate(R.id.action_loginFragment_to_bibliotecaFragment)
        }
    }
}