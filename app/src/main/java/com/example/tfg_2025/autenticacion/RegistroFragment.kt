package com.example.tfg_2025.ui.autenticacion

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.tfg_2025.R

class RegistroFragment : Fragment(R.layout.fragment_registro) {

    override fun onViewCreated(vista: View, savedInstanceState: Bundle?) {
        super.onViewCreated(vista, savedInstanceState)

        // Referencias a los componentes del XML
        val etNombre = vista.findViewById<EditText>(R.id.et_reg_nombre)
        val etEmail = vista.findViewById<EditText>(R.id.et_reg_email)
        val etPass = vista.findViewById<EditText>(R.id.et_reg_pass)
        val etPassRep = vista.findViewById<EditText>(R.id.et_reg_pass_rep)
        val checkTerminos = vista.findViewById<CheckBox>(R.id.check_terminos)
        val botonRegistrar = vista.findViewById<Button>(R.id.boton_registrar)
        val enlaceLogin = vista.findViewById<TextView>(R.id.texto_volver_login)

        botonRegistrar.setOnClickListener {
            val nombre = etNombre.text.toString()
            val email = etEmail.text.toString()
            val pass = etPass.text.toString()
            val passRep = etPassRep.text.toString()

            // Lógica de validación
            when {
                nombre.isEmpty() || email.isEmpty() || pass.isEmpty() -> {
                    Toast.makeText(requireContext(), "Rellena todos los campos", Toast.LENGTH_SHORT).show()
                }
                pass != passRep -> {
                    Toast.makeText(requireContext(), "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                }
                !checkTerminos.isChecked -> {
                    Toast.makeText(requireContext(), "Debes aceptar los términos", Toast.LENGTH_SHORT).show()
                }
                else -> {

                    Toast.makeText(requireContext(), "¡Registro con éxito, bienvenido $nombre!", Toast.LENGTH_LONG).show()
                    findNavController().navigate(R.id.action_registroFragment_to_bibliotecaFragment)
                }
            }
        }

        //vuelve a login si ya tiene cuenta
        enlaceLogin.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}