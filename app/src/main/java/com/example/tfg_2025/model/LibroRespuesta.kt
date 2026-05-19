package com.example.tfg_2025.model

import com.google.gson.annotations.SerializedName

data class LibroRespuesta(
    @SerializedName("docs") val items: List<BookItem>?
)

data class BookItem(
    @SerializedName("key") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("author_name") val authors: List<String>?,
    @SerializedName("cover_i") val coverId: Int?
) {
    // Propiedad calculada para simular el formato que ya lee tu repositorio
    val volumeInfo: VolumeInfo
        get() = VolumeInfo(
            title = this.title,
            authors = this.authors,
            imageLinks = this.coverId?.let { ImageLinks("https://covers.openlibrary.org/b/id/$it-M.jpg") }
        )
}

data class VolumeInfo(
    val title: String?,
    val authors: List<String>?,
    val imageLinks: ImageLinks?
)

data class ImageLinks(
    val thumbnail: String?
)