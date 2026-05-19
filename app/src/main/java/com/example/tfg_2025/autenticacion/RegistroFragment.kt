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
import com.google.firebase.auth.FirebaseAuth
import java.util.Calendar

class RegistroFragment : Fragment(R.layout.fragment_registro) {

    private lateinit var auth: FirebaseAuth

    override fun onViewCreated(vista: View, savedInstanceState: Bundle?) {
        super.onViewCreated(vista, savedInstanceState)

        auth = FirebaseAuth.getInstance()

        // Referencias a tus componentes del XML
        val etNombre = vista.findViewById<EditText>(R.id.et_reg_nombre)
        val etEmail = vista.findViewById<EditText>(R.id.et_reg_email)
        val etPass = vista.findViewById<EditText>(R.id.et_reg_pass)
        val etPassRep = vista.findViewById<EditText>(R.id.et_reg_pass_rep)
        val etAnio = vista.findViewById<EditText>(R.id.et_reg_anio)
        val checkTerminos = vista.findViewById<CheckBox>(R.id.check_terminos)
        val botonRegistrar = vista.findViewById<Button>(R.id.boton_registrar)
        val enlaceLogin = vista.findViewById<TextView>(R.id.texto_volver_login)

        botonRegistrar.setOnClickListener {
            val nombre = etNombre.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val pass = etPass.text.toString().trim()
            val passRep = etPassRep.text.toString().trim()
            val anioStr = etAnio.text.toString().trim()

            // Validamos primero que el ano no este vacio para poder calcular la edad
            val anioNacimiento = anioStr.toIntOrNull() ?: 0
            val anioActual = Calendar.getInstance().get(Calendar.YEAR)
            val edad = anioActual - anioNacimiento

            // Logica de validacion ORDENADA y ESTRICTA
            when {
                nombre.isEmpty() || email.isEmpty() || pass.isEmpty() || anioStr.isEmpty() -> {
                    Toast.makeText(requireContext(), "Por favor, rellena todos los campos", Toast.LENGTH_SHORT).show()
                }
                pass.length < 6 -> {
                    Toast.makeText(requireContext(), "La contrasena debe tener minimo 6 caracteres", Toast.LENGTH_SHORT).show()
                }
                pass != passRep -> {
                    Toast.makeText(requireContext(), "Las contrasenas no coinciden", Toast.LENGTH_SHORT).show()
                }
                !checkTerminos.isChecked -> {
                    // SOLUCION TERMINOS: Si no esta marcado, saltara este aviso y se detiene aqui
                    Toast.makeText(requireContext(), "Debes aceptar los terminos y condiciones para continuar", Toast.LENGTH_LONG).show()
                }
                edad < 16 -> {
                    // SOLUCION EDAD: Si calculamos que es menor de 16, bloqueamos el registro
                    Toast.makeText(requireContext(), "Lo sentimos, debes ser mayor de 16 anos para registrarte", Toast.LENGTH_LONG).show()
                }
                else -> {
                    // Si todo esta perfecto, procedemos a Firebase
                    auth.createUserWithEmailAndPassword(email, pass)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                try {
                                    Toast.makeText(requireContext(), "¡Registro con exito, bienvenido $nombre!", Toast.LENGTH_LONG).show()

                                    // Configuranos las opciones para limpiar el historial de navegacion (Login y Registro)
                                    val opciones = androidx.navigation.NavOptions.Builder()
                                        .setPopUpTo(R.id.loginFragment, true)
                                        .build()

                                    // Navegamos a la biblioteca usando la ruta declarada en tu nav_graph
                                    findNavController().navigate(
                                        R.id.action_registroFragment_to_bibliotecaFragment,
                                        null,
                                        opciones
                                    )

                                } catch (e: Exception) {
                                    Toast.makeText(requireContext(), "Error de navegacion: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                                }
                            } else {
                                Toast.makeText(requireContext(), "Error Firebase: ${task.exception?.localizedMessage}", Toast.LENGTH_LONG).show()
                            }
                        }
                }
            }
        }

        // Volver al login usando Jetpack Navigation de forma segura
        enlaceLogin.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}