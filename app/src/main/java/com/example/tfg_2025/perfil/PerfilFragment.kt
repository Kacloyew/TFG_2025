package com.example.tfg_2025.ui.perfil

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.tfg_2025.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class PerfilFragment : Fragment(R.layout.fragment_perfil) {

    override fun onViewCreated(vista: View, savedInstanceState: Bundle?) {
        super.onViewCreated(vista, savedInstanceState)

        val botonSalir = vista.findViewById<Button>(R.id.boton_cerrar_sesion)
        val botonEditar = vista.findViewById<Button>(R.id.boton_editar_perfil)
        val tvNombre = vista.findViewById<TextView>(R.id.nombre_usuario)
        val tvCorreo = vista.findViewById<TextView>(R.id.tv_correo_perfil)
        val tvBio = vista.findViewById<TextView>(R.id.tv_bio_perfil)
        val tvWeb = vista.findViewById<TextView>(R.id.tv_web_perfil)
        val imgAvatar = vista.findViewById<ImageView>(R.id.img_perfil_avatar)

        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser

        tvCorreo.text = user?.email ?: ""

        if (user != null) {
            FirebaseFirestore.getInstance()
                .collection("usuarios")
                .document(user.uid)
                .get()
                .addOnSuccessListener { doc ->
                    if (doc != null && doc.exists()) {
                        // Carga de textos
                        tvNombre.text = doc.getString("nombre") ?: user.email ?: "Usuario"
                        tvBio.text = doc.getString("bio") ?: ""
                        tvWeb.text = doc.getString("web") ?: ""

                        // Carga de imagen DENTRO del bloque para tener acceso a 'doc'
                        val fotoUrl = doc.getString("fotoUrl")
                        if (!fotoUrl.isNullOrEmpty()) {
                            Glide.with(requireContext())
                                .load(fotoUrl)
                                .placeholder(R.drawable.ic_menu_libro) // Pon un placeholder tuyo
                                .circleCrop()
                                .into(imgAvatar)
                        }
                    } else {
                        tvNombre.text = user.email ?: "Usuario"
                    }
                }
                .addOnFailureListener {
                    tvNombre.text = user.email ?: "Usuario"
                    Toast.makeText(requireContext(), "Error cargando perfil", Toast.LENGTH_SHORT).show()
                }
        }

        botonEditar?.setOnClickListener {
            findNavController().navigate(R.id.action_perfilFragment_to_editarPerfilFragment)
        }

        botonSalir.setOnClickListener {
            auth.signOut()
            // Usamos navOptions para limpiar la pila al salir y evitar el error de NavController
            findNavController().navigate(R.id.action_perfilFragment_to_loginFragment)
        }
    }
}