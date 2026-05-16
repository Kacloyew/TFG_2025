package com.example.tfg_2025.ui.autenticacion

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.tfg_2025.MainActivity
import com.example.tfg_2025.R
import com.example.tfg_2025.ui.biblioteca.BibliotecaFragment
import com.example.tfg_2025.ui.autenticacion.RegistroFragment
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

        // 3. Lógica del botón Ingresar con Firebase
        botonIngresar.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val pass = etPassword.text.toString().trim()

            if (email.isNotEmpty() && pass.isNotEmpty()) {
                auth.signInWithEmailAndPassword(email, pass)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            try {
                                // 1. Avisamos a la MainActivity para que muestre el menú inferior
                                val mainAct = activity as? MainActivity
                                mainAct?.loginExitoso()

                                // 2. En lugar de usar findNavController, usamos el método de tu MainActivity
                                parentFragmentManager.beginTransaction()
                                    .replace(R.id.nav_host_fragment, BibliotecaFragment())
                                    .commit()

                            } catch (e: Exception) {
                                Toast.makeText(requireContext(), "Error post-login: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                            }
                        } else {
                            Toast.makeText(requireContext(), "Error Firebase: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(requireContext(), "Por favor, rellena todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        // 4. Ir a la pantalla de registro usando transacciones manuales
        tvRegistro.setOnClickListener {
            try {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.nav_host_fragment, RegistroFragment())
                    .addToBackStack(null) // Permite volver atrás al Login si el usuario pulsa el botón físico de atrás
                    .commit()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error ir a registro: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}