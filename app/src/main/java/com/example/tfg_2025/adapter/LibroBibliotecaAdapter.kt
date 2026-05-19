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

class LibroBibliotecaAdapter(
    private var libros: MutableList<Libro>,
    private val onFavoritoClick: (Libro) -> Unit,
    private val onItemClick: (Libro) -> Unit,
    private val esHorizontal: Boolean = false
) : RecyclerView.Adapter<LibroBibliotecaAdapter.BibliotecaViewHolder>() {

    class BibliotecaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val portada: ImageView = view.findViewById(R.id.img_item_biblioteca_portada)
        val titulo: TextView = view.findViewById(R.id.tv_item_biblioteca_titulo)
        val estrella: ImageView = view.findViewById(R.id.img_favoritos)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BibliotecaViewHolder {
        val layoutId = if (esHorizontal) R.layout.item_libro_biblioteca_horizontal
        else R.layout.item_libro_biblioteca
        val view = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
        return BibliotecaViewHolder(view)
    }

    override fun onBindViewHolder(holder: BibliotecaViewHolder, position: Int) {
        val libro = libros[position]
        holder.titulo.text = libro.titulo

        if (!libro.urlPortada.isNullOrEmpty()) {
            Glide.with(holder.itemView.context).load(libro.urlPortada).into(holder.portada)
        }

        val icono = if (libro.esFavorito) android.R.drawable.btn_star_big_on else android.R.drawable.btn_star_big_off
        holder.estrella.setImageResource(icono)

        holder.itemView.setOnClickListener {
            onItemClick(libro)
        }

        holder.estrella.setOnClickListener {
            libro.esFavorito = !libro.esFavorito
            onFavoritoClick(libro)
            val nuevoIcono = if (libro.esFavorito) android.R.drawable.btn_star_big_on else android.R.drawable.btn_star_big_off
            holder.estrella.setImageResource(nuevoIcono)
        }
    }

    override fun getItemCount(): Int = libros.size

    fun updateList(nuevaLista: List<Libro>) {
        this.libros.clear()
        this.libros.addAll(nuevaLista)
        notifyDataSetChanged()
    }
}