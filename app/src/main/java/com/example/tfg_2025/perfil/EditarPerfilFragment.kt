package com.example.tfg_2025.ui.perfil

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.tfg_2025.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class EditarPerfilFragment : Fragment(R.layout.fragment_editar_perfil) {

    private var imagenSeleccionada: Uri? = null

    // Lanzador para abrir la galería
    private val seleccionarImagen = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            imagenSeleccionada = result.data?.data
            view?.findViewById<ImageView>(R.id.img_edit_avatar)?.let { imgView ->
                Glide.with(requireContext())
                    .load(imagenSeleccionada)
                    .circleCrop()
                    .into(imgView)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val imgAvatar = view.findViewById<ImageView>(R.id.img_edit_avatar)
        val tvCambiarFoto = view.findViewById<TextView>(R.id.tv_cambiar_foto)
        val etNombre = view.findViewById<EditText>(R.id.et_edit_nombre)
        val etBio = view.findViewById<EditText>(R.id.et_edit_bio)
        val etWeb = view.findViewById<EditText>(R.id.et_edit_web)
        val btnGuardar = view.findViewById<Button>(R.id.btn_guardar_cambios)

        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        val db = FirebaseFirestore.getInstance()
        val storage = FirebaseStorage.getInstance()

        // Cargar datos actuales
        if (user != null) {
            db.collection("usuarios").document(user.uid)
                .get()
                .addOnSuccessListener { doc ->
                    if (doc != null && doc.exists()) {
                        etNombre.setText(doc.getString("nombre") ?: "")
                        etBio.setText(doc.getString("bio") ?: "")
                        etWeb.setText(doc.getString("web") ?: "")
                        val fotoUrl = doc.getString("fotoUrl")
                        if (!fotoUrl.isNullOrEmpty()) {
                            Glide.with(requireContext())
                                .load(fotoUrl)
                                .circleCrop()
                                .into(imgAvatar)
                        }
                    }
                }
        }

        // Abrir galería al pulsar la foto o el texto
        val abrirGaleria = {
            val intent = Intent(Intent.ACTION_PICK).apply { type = "image/*" }
            seleccionarImagen.launch(intent)
        }
        imgAvatar.setOnClickListener { abrirGaleria() }
        tvCambiarFoto.setOnClickListener { abrirGaleria() }

        // Guardar cambios
        btnGuardar.setOnClickListener {
            val nombre = etNombre.text.toString().trim()
            val bio = etBio.text.toString().trim()
            val web = etWeb.text.toString().trim()

            if (nombre.isEmpty()) {
                etNombre.error = "El nombre no puede estar vacío"
                return@setOnClickListener
            }

            if (user == null) return@setOnClickListener

            btnGuardar.isEnabled = false
            btnGuardar.text = "Guardando..."

            // Si hay imagen nueva, subirla primero
            if (imagenSeleccionada != null) {
                val ref = storage.reference.child("avatares/${user.uid}.jpg")
                ref.putFile(imagenSeleccionada!!)
                    .addOnSuccessListener {
                        ref.downloadUrl.addOnSuccessListener { url ->
                            guardarEnFirestore(db, user.uid, nombre, bio, web, url.toString()) {
                                btnGuardar.isEnabled = true
                                btnGuardar.text = "Guardar cambios"
                            }
                        }
                    }
                    .addOnFailureListener {
                        btnGuardar.isEnabled = true
                        btnGuardar.text = "Guardar cambios"
                        Toast.makeText(requireContext(), "Error subiendo la foto", Toast.LENGTH_SHORT).show()
                    }
            } else {
                // Sin imagen nueva, guardar solo los textos
                guardarEnFirestore(db, user.uid, nombre, bio, web, null) {
                    btnGuardar.isEnabled = true
                    btnGuardar.text = "Guardar cambios"
                }
            }
        }
    }

    private fun guardarEnFirestore(
        db: FirebaseFirestore,
        uid: String,
        nombre: String,
        bio: String,
        web: String,
        fotoUrl: String?,
        onComplete: () -> Unit
    ) {
        val datos = hashMapOf<String, Any>(
            "nombre" to nombre,
            "bio" to bio,
            "web" to web
        )
        if (fotoUrl != null) datos["fotoUrl"] = fotoUrl

        db.collection("usuarios").document(uid)
            .set(datos)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Perfil actualizado", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Error al guardar", Toast.LENGTH_SHORT).show()
            }
            .addOnCompleteListener { onComplete() }
    }
}