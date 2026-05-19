package com.example.tfg_2025.ui.autenticacion

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.tfg_2025.R
import com.google.firebase.auth.FirebaseAuth

class LoginFragment : Fragment(R.layout.fragment_login) {

    private lateinit var auth: FirebaseAuth

    override fun onViewCreated(vista: View, savedInstanceState: Bundle?) {
        super.onViewCreated(vista, savedInstanceState)

        // 1. Inicializar Firebase
        auth = FirebaseAuth.getInstance()

        // 2. Referencias a la vista
        val etEmail = vista.findViewById<EditText>(R.id.et_login_email)
        val etPassword = vista.findViewById<EditText>(R.id.et_login_password)
        val botonIngresar = vista.findViewById<Button>(R.id.boton_ingresar)
        val tvRegistro = vista.findViewById<TextView>(R.id.tv_ir_registro)

        // 3. Logica del boton Ingresar con Firebase
        botonIngresar.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val pass = etPassword.text.toString().trim()

            if (email.isNotEmpty() && pass.isNotEmpty()) {
                auth.signInWithEmailAndPassword(email, pass)
                    .addOnCompleteListener { task ->


                        if (!isAdded || context == null) return@addOnCompleteListener

                        if (task.isSuccessful) {
                            try {
                                // Creamos las opciones para borrar el Login del historial
                                val opciones = androidx.navigation.NavOptions.Builder()
                                    .setPopUpTo(R.id.loginFragment, true)
                                    .build()

                                // Navegamos usando la accion oficial de tu nav_graph
                                findNavController().navigate(
                                    R.id.action_loginFragment_to_bibliotecaFragment,
                                    null,
                                    opciones
                                )

                            } catch (e: Exception) {
                                // Doble check de seguridad
                                if (isAdded && context != null) {
                                    Toast.makeText(requireContext(), "Error post-login: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                                }
                            }
                        } else {

                            Toast.makeText(requireContext(), "Error Firebase: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(requireContext(), "Por favor, rellena todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        // 4. Ir a la pantalla de registro usando la accion oficial del grafo
        tvRegistro.setOnClickListener {
            try {
                findNavController().navigate(R.id.action_loginFragment_to_registroFragment)
            } catch (e: Exception) {
                if (isAdded && context != null) {
                    Toast.makeText(requireContext(), "Error ir a registro: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}