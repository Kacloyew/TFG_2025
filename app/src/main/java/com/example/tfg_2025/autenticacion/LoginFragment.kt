package com.example.tfg_2025.ui.autenticacion

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.tfg_2025.R

class LoginFragment : Fragment(R.layout.fragment_login) {

    override fun onViewCreated(vista: View, savedInstanceState: Bundle?) {
        super.onViewCreated(vista, savedInstanceState)

        val etEmail = vista.findViewById<EditText>(R.id.et_login_email)
        val etPassword = vista.findViewById<EditText>(R.id.et_login_password)
        val botonIngresar = vista.findViewById<Button>(R.id.boton_ingresar)
        val tvRegistro = vista.findViewById<View>(R.id.tv_ir_registro)


        botonIngresar.setOnClickListener {
            val email = etEmail.text.toString()
            val pass = etPassword.text.toString()

            if (email.isNotEmpty() && pass.isNotEmpty()) {

                findNavController().navigate(R.id.action_loginFragment_to_bibliotecaFragment)
            } else {

                Toast.makeText(requireContext(), "Por favor, rellena todos los campos", Toast.LENGTH_SHORT).show()
            }
        }


        tvRegistro.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registroFragment)
        }
    }
}