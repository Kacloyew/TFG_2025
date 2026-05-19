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
import com.example.tfg_2025.model.Libro
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DetalleLibroFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_detalle_libro, container, false)
    }

    override fun onViewCreated(vista: View, savedInstanceState: Bundle?) {
        super.onViewCreated(vista, savedInstanceState)

        val idLibro = arguments?.getString("id_libro") ?: ""
        val titulo = arguments?.getString("titulo_libro") ?: "Sin título"
        val autor = arguments?.getString("autor_libro") ?: "Autor desconocido"
        val urlPortada = arguments?.getString("portada_libro") ?: ""

        val tvTitulo = vista.findViewById<TextView>(R.id.tv_detalle_titulo)
        val tvAutor = vista.findViewById<TextView>(R.id.tv_detalle_autor)
        val imgPortada = vista.findViewById<ImageView>(R.id.img_detalle_portada)
        val botonAñadir = vista.findViewById<Button>(R.id.btn_añadir_biblioteca)
        val switchLeyendo = vista.findViewById<SwitchCompat>(R.id.switch_leyendo)
        val switchLeido = vista.findViewById<SwitchCompat>(R.id.switch_leido)
        val switchPendiente = vista.findViewById<SwitchCompat>(R.id.switch_pendiente)
        val botonEliminar = vista.findViewById<Button>(R.id.btn_eliminar_biblioteca)
        val botonAtras = vista.findViewById<Button>(R.id.btn_detalle_atras)

        // ← CAMBIO: de requireActivity().finish() a popBackStack()
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

        val database = AppDatabase.getDatabase(requireContext())
        val libroDao = database.libroDao()

        var switchesInicializados = false

        // 1. Carga inicial del estado desde la BD
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            val libroExistente = libroDao.obtenerLibroPorId(idLibro)

            withContext(Dispatchers.Main) {
                if (libroExistente != null) {
                    switchesInicializados = false
                    switchLeyendo?.isChecked = libroExistente.leyendo
                    switchLeido?.isChecked = libroExistente.leido
                    switchPendiente?.isChecked = libroExistente.pendiente
                    switchesInicializados = true

                    if (libroExistente.estaEnEstanteria) {
                        botonAñadir?.visibility = View.GONE
                        botonEliminar?.visibility = View.VISIBLE
                    } else {
                        botonEliminar?.visibility = View.GONE
                    }
                } else {
                    switchesInicializados = true
                    botonEliminar?.visibility = View.GONE
                }
            }
        }

        // 2. Switches
        val guardarEstadoSwitches = {
            if (switchesInicializados) {
                viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                    val libro = libroDao.obtenerLibroPorId(idLibro)
                    if (libro != null) {
                        libroDao.insertarLibro(
                            libro.copy(
                                leyendo = switchLeyendo?.isChecked ?: false,
                                leido = switchLeido?.isChecked ?: false,
                                pendiente = switchPendiente?.isChecked ?: false
                            )
                        )
                    }
                }
            }
        }

        switchLeyendo?.setOnCheckedChangeListener { _, _ -> guardarEstadoSwitches() }
        switchLeido?.setOnCheckedChangeListener { _, _ -> guardarEstadoSwitches() }
        switchPendiente?.setOnCheckedChangeListener { _, _ -> guardarEstadoSwitches() }

        // 3. Botón Añadir
        botonAñadir?.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                val libroExistente = libroDao.obtenerLibroPorId(idLibro)
                val libroAGuardar = libroExistente?.copy(estaEnEstanteria = true) ?: Libro(
                    id = idLibro, titulo = titulo, autor = autor, urlPortada = urlPortada,
                    esFavorito = false, estaEnEstanteria = true
                )
                libroDao.insertarLibro(libroAGuardar)

                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "¡Añadido a tu librería!", Toast.LENGTH_SHORT).show()
                    botonAñadir.visibility = View.GONE
                    botonEliminar?.visibility = View.VISIBLE
                }
            }
        }

        // 4. Botón Eliminar
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
                        Toast.makeText(requireContext(), "Eliminado de la biblioteca", Toast.LENGTH_SHORT).show()
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
}