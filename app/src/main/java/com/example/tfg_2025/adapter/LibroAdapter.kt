package com.example.tfg_2025.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tfg_2025.R
import com.example.tfg_2025.model.BookItem

class LibroAdapter(private val libros: List<BookItem>) : RecyclerView.Adapter<LibroAdapter.LibroViewHolder>() {

    class LibroViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titulo: TextView = view.findViewById(R.id.tv_item_titulo)
        val autor: TextView = view.findViewById(R.id.tv_item_autor)
        val portada: ImageView = view.findViewById(R.id.iv_item_portada)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LibroViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_libro, parent, false)
        return LibroViewHolder(view)
    }

    override fun onBindViewHolder(holder: LibroViewHolder, position: Int) {
        val libro = libros[position]
        holder.titulo.text = libro.volumeInfo.title
        holder.autor.text = libro.volumeInfo.authors?.joinToString(", ") ?: "Autor desconocido"

        // Usamos Glide para cargar la imagen de internet
        val url = libro.volumeInfo.imageLinks?.thumbnail?.replace("http://", "https://")
        Glide.with(holder.itemView.context).load(url).into(holder.portada)
    }

    override fun getItemCount() = libros.size
}