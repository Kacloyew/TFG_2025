package com.example.tfg_2025.model

class LibroRespuesta {

    data class GoogleBooksResponse(
        val items: List<BookItem>?
    )

    data class BookItem(
        val id: String,
        val volumeInfo: VolumeInfo
    )

    data class VolumeInfo(
        val title: String,
        val authors: List<String>?,
        val description: String?,
        val imageLinks: ImageLinks?
    )

    data class ImageLinks(
        val thumbnail: String
    )
}