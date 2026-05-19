package com.example.tfg_2025.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tfg_2025.R
import com.example.tfg_2025.data.AppDatabase
import com.example.tfg_2025.model.Libro
import com.example.tfg_2025.ui.biblioteca.DetalleLibroFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LibroAdapter(private var libros: MutableList<Libro>) :
    RecyclerView.Adapter<LibroAdapter.LibroViewHolder>() {

    class LibroViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titulo: TextView = view.findViewById(R.id.tv_libro_titulo)
        val autor: TextView = view.findViewById(R.id.tv_libro_autor)
        val portada: ImageView = view.findViewById(R.id.img_libro_portada)
        val iconoFavorito: ImageView = view.findViewById(R.id.img_favorito_icono)
    }

    fun updateList(nuevaLista: List<Libro>) {
        this.libros.clear()
        this.libros.addAll(nuevaLista)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LibroViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_libro_busqueda, parent, false)
        return LibroViewHolder(view)
    }

    override fun onBindViewHolder(holder: LibroViewHolder, position: Int) {
        val libro = libros[position]
        val contexto = holder.itemView.context

        holder.titulo.text = libro.titulo ?: "Sin título"
        holder.autor.text = libro.autor ?: "Autor desconocido"

        if (!libro.urlPortada.isNullOrEmpty()) {
            Glide.with(contexto)
                .load(libro.urlPortada)
                .placeholder(R.drawable.ic_menu_libro)
                .error(R.drawable.ic_menu_libro)
                .into(holder.portada)
        } else {
            holder.portada.setImageResource(R.drawable.ic_menu_libro)
        }

        val database = AppDatabase.getDatabase(contexto)
        val libroDao = database.libroDao()

        // Comprobamos el estado real del libro en la base de datos para pintar la estrella correctamente
        CoroutineScope(Dispatchers.IO).launch {
            val libroEnBD = libroDao.obtenerLibroPorId(libro.id)
            val existeEnFavs = libroEnBD?.esFavorito == true

            withContext(Dispatchers.Main) {
                if (existeEnFavs) {
                    holder.iconoFavorito.setImageResource(android.R.drawable.btn_star_big_on)
                    holder.iconoFavorito.tag = "on"
                } else {
                    holder.iconoFavorito.setImageResource(android.R.drawable.btn_star_big_off)
                    holder.iconoFavorito.tag = "off"
                }
            }
        }

        // Configuración del botón de la estrella (Favoritos)
        holder.iconoFavorito.setOnClickListener {
            val estadoActual = holder.iconoFavorito.tag as? String ?: "off"

            if (estadoActual == "off") {
                holder.iconoFavorito.setImageResource(android.R.drawable.btn_star_big_on)
                holder.iconoFavorito.tag = "on"
                Toast.makeText(contexto, "Guardado en Favoritos", Toast.LENGTH_SHORT).show()

                CoroutineScope(Dispatchers.IO).launch {
                    val libroExistente = libroDao.obtenerLibroPorId(libro.id)
                    // Si ya existía (por estar en la estantería), activamos esFavorito conservando la estantería
                    val libroAGuardar = libroExistente?.copy(esFavorito = true)
                        ?: libro.copy(esFavorito = true, estaEnEstanteria = false)

                    libroDao.insertarLibro(libroAGuardar)
                }
            } else {
                holder.iconoFavorito.setImageResource(android.R.drawable.btn_star_big_off)
                holder.iconoFavorito.tag = "off"
                Toast.makeText(contexto, "Eliminado de Favoritos", Toast.LENGTH_SHORT).show()

                CoroutineScope(Dispatchers.IO).launch {
                    val libroExistente = libroDao.obtenerLibroPorId(libro.id)
                    if (libroExistente != null) {
                        if (libroExistente.estaEnEstanteria) {
                            // Si está en la estantería, NO lo borramos, solo le apagamos el favorito
                            val libroModificado = libroExistente.copy(esFavorito = false)
                            libroDao.insertarLibro(libroModificado)
                        } else {
                            // Si tampoco estaba en la estantería, lo borramos de la base de datos por completo
                            libroDao.eliminarLibro(libroExistente)
                        }
                    }
                }
            }
        }

        holder.itemView.setOnClickListener {
            val actividad = contexto as? AppCompatActivity
            if (actividad != null) {
                val fragmentoDetalle = DetalleLibroFragment()

                val datos = Bundle().apply {
                    putString("id_libro", libro.id ?: "")
                    putString("titulo_libro", libro.titulo ?: "Sin título")
                    putString("autor_libro", libro.autor ?: "Autor desconocido")
                    putString("portada_libro", libro.urlPortada ?: "")
                }
                fragmentoDetalle.arguments = datos

                actividad.supportFragmentManager.beginTransaction()
                    .replace(R.id.nav_host_fragment, fragmentoDetalle)
                    .addToBackStack(null)
                    .commit()
            }
        }
    }

    override fun getItemCount(): Int = libros.size
}