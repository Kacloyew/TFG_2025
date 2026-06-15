package com.example.tfg_2025.ui.biblioteca

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.tfg_2025.R
import com.example.tfg_2025.data.AppDatabase
import com.example.tfg_2025.data.LibroDao
import com.example.tfg_2025.model.Libro
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DetalleLibroFragment : Fragment() {

    private lateinit var switchLeyendo: SwitchCompat
    private lateinit var switchLeido: SwitchCompat
    private lateinit var switchPendiente: SwitchCompat
    private lateinit var libroDao: LibroDao
    private var idLibro: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_detalle_libro, container, false)
    }

    override fun onViewCreated(vista: View, savedInstanceState: Bundle?) {
        super.onViewCreated(vista, savedInstanceState)


        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        libroDao = AppDatabase.getDatabase(requireContext(), userId).libroDao()

        idLibro = arguments?.getString("id_libro") ?: ""
        val titulo = arguments?.getString("titulo_libro") ?: "Sin título"
        val autor = arguments?.getString("autor_libro") ?: "Autor desconocido"
        val urlPortada = arguments?.getString("portada_libro") ?: ""

        val tvTitulo = vista.findViewById<TextView>(R.id.tv_detalle_titulo)
        val tvAutor = vista.findViewById<TextView>(R.id.tv_detalle_autor)
        val imgPortada = vista.findViewById<ImageView>(R.id.img_detalle_portada)
        val botonAñadir = vista.findViewById<Button>(R.id.btn_añadir_biblioteca)
        val botonEliminar = vista.findViewById<Button>(R.id.btn_eliminar_biblioteca)
        val botonAtras = vista.findViewById<Button>(R.id.btn_detalle_atras)

        switchLeyendo = vista.findViewById(R.id.switch_leyendo)
        switchLeido = vista.findViewById(R.id.switch_leido)
        switchPendiente = vista.findViewById(R.id.switch_pendiente)

        botonAtras.setOnClickListener {
            findNavController().popBackStack()
        }

        tvTitulo?.text = titulo
        tvAutor?.text = autor

        if (imgPortada != null && urlPortada.isNotEmpty()) {
            Glide.with(requireContext())
                .load(urlPortada)
                .placeholder(R.drawable.ic_menu_libro)
                .error(R.drawable.ic_menu_libro)
                .into(imgPortada)
        }

        var switchesInicializados = false

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            val libroExistente = libroDao.obtenerLibroPorId(idLibro)
            withContext(Dispatchers.Main) {
                if (libroExistente != null) {
                    switchLeyendo.isChecked = libroExistente.leyendo
                    switchLeido.isChecked = libroExistente.leido
                    switchPendiente.isChecked = libroExistente.pendiente
                    botonAñadir?.visibility =
                        if (libroExistente.estaEnEstanteria) View.GONE else View.VISIBLE
                    botonEliminar?.visibility =
                        if (libroExistente.estaEnEstanteria) View.VISIBLE else View.GONE
                } else {
                    botonEliminar?.visibility = View.GONE
                }
            }
        }

        switchLeyendo.setOnClickListener {
            if (switchLeyendo.isChecked) {
                switchLeido.isChecked = false
                switchPendiente.isChecked = false
            }
            guardarEstadoSwitches()
        }

        switchLeido.setOnClickListener {
            if (switchLeido.isChecked) {
                switchLeyendo.isChecked = false
                switchPendiente.isChecked = false
            }
            guardarEstadoSwitches()
        }

        switchPendiente.setOnClickListener {
            if (switchPendiente.isChecked) {
                switchLeyendo.isChecked = false
                switchLeido.isChecked = false
            }
            guardarEstadoSwitches()
        }

        botonAñadir?.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                val libroExistente = libroDao.obtenerLibroPorId(idLibro)
                val libroAGuardar = libroExistente?.copy(estaEnEstanteria = true) ?: Libro(
                    id = idLibro, titulo = titulo, autor = autor, urlPortada = urlPortada,
                    esFavorito = false, estaEnEstanteria = true
                )
                libroDao.insertarLibro(libroAGuardar)

                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "¡Añadido a tu librería!", Toast.LENGTH_SHORT)
                        .show()
                    botonAñadir.visibility = View.GONE
                    botonEliminar?.visibility = View.VISIBLE
                }
            }
        }

        botonEliminar?.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                val libroExistente = libroDao.obtenerLibroPorId(idLibro)
                if (libroExistente != null) {
                    if (libroExistente.esFavorito) {
                        libroDao.insertarLibro(
                            libroExistente.copy(
                                estaEnEstanteria = false,
                                leyendo = false, leido = false, pendiente = false
                            )
                        )
                    } else {
                        libroDao.eliminarLibro(libroExistente)
                    }

                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            requireContext(),
                            "Eliminado de la biblioteca",
                            Toast.LENGTH_SHORT
                        ).show()
                        switchesInicializados = false
                        switchLeyendo?.isChecked = false
                        switchLeido?.isChecked = false
                        switchPendiente?.isChecked = false
                        switchesInicializados = true
                        botonAñadir?.visibility = View.VISIBLE
                        botonEliminar?.visibility = View.GONE
                    }
                }
            }
        }
    }

    private fun guardarEstadoSwitches() {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            val libro = libroDao.obtenerLibroPorId(idLibro)

            val libroAGuardar = libro?.copy(
                estaEnEstanteria = true,  // ← forzar siempre
                leyendo = switchLeyendo.isChecked,
                leido = switchLeido.isChecked,
                pendiente = switchPendiente.isChecked
            ) ?: Libro(
                id = idLibro,
                titulo = arguments?.getString("titulo_libro") ?: "",
                autor = arguments?.getString("autor_libro") ?: "",
                urlPortada = arguments?.getString("portada_libro") ?: "",
                esFavorito = false,
                estaEnEstanteria = true,  // ← forzar siempre
                leyendo = switchLeyendo.isChecked,
                leido = switchLeido.isChecked,
                pendiente = switchPendiente.isChecked
            )

            libroDao.insertarLibro(libroAGuardar)

            withContext(Dispatchers.Main) {
                // Solo mostramos botón eliminar si está en estantería
                val enEstanteria = libroAGuardar.estaEnEstanteria
                view?.findViewById<Button>(R.id.btn_añadir_biblioteca)?.visibility =
                    if (enEstanteria) View.GONE else View.VISIBLE
                view?.findViewById<Button>(R.id.btn_eliminar_biblioteca)?.visibility =
                    if (enEstanteria) View.VISIBLE else View.GONE
            }
        }
    }
}