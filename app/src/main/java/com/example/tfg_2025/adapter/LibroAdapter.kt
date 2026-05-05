package com.example.tfg_2025.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tfg_2025.R
import com.example.tfg_2025.model.Libro

class LibroAdapter(private val libros: List<Libro>) : RecyclerView.Adapter<LibroAdapter.LibroViewHolder>() {

    class LibroViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titulo: TextView = view.findViewById(R.id.tv_libro_titulo)
        val autor: TextView = view.findViewById(R.id.tv_libro_autor)
        val portada: ImageView = view.findViewById(R.id.img_libro_portada)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LibroViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_libro, parent, false)
        return LibroViewHolder(view)
    }

    override fun onBindViewHolder(holder: LibroViewHolder, position: Int) {
        val libro = libros[position]

        holder.titulo.text = libro.titulo ?: "Sin título"
        holder.autor.text = libro.autor ?: "Autor desconocido"

        // Cargamos la imagen
        val url = libro.urlPortada?.replace("http://", "https://")

        Glide.with(holder.itemView.context)
            .load(url)
            .placeholder(android.R.drawable.ic_menu_gallery)
            .error(android.R.drawable.stat_notify_error)
            .into(holder.portada)
    }

    override fun getItemCount() = libros.size
}