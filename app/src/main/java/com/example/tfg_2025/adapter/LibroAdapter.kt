package com.example.tfg_2025.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tfg_2025.R
import com.example.tfg_2025.model.LibroRespuesta // Importamos el nuevo modelo

class LibroAdapter(private var libros: List<LibroRespuesta.BookItem>) :
    RecyclerView.Adapter<LibroAdapter.LibroViewHolder>() {

    class LibroViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titulo: TextView = view.findViewById(R.id.tv_libro_titulo)
        val autor: TextView = view.findViewById(R.id.tv_libro_autor)
        val portada: ImageView = view.findViewById(R.id.img_libro_portada)
    }

    fun updateList(nuevaLista: List<LibroRespuesta.BookItem>) {
        this.libros = nuevaLista
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LibroViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_libro, parent, false)
        return LibroViewHolder(view)
    }

    override fun onBindViewHolder(holder: LibroViewHolder, position: Int) {
        val libro = libros[position]
        val info = libro.volumeInfo

        holder.titulo.text = info?.title ?: "Sin título"

        // Unimos la lista de autores en un solo texto separado por comas
        holder.autor.text = info?.authors?.joinToString(", ") ?: "Autor desconocido"

        // Sacamos la URL de la miniatura
        val url = info?.imageLinks?.thumbnail?.replace("http://", "https://")

        Glide.with(holder.itemView.context)
            .load(url)
            .placeholder(R.drawable.ic_menu_libro)
            .error(R.drawable.ic_menu_libro)
            .into(holder.portada)
    }

    override fun getItemCount() = libros.size
}