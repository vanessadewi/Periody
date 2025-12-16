package com.example.periody.model

import kotlinx.serialization.Serializable

@Serializable
data class Artikel(
    val id: String? = null,              // UUID auto
    val judul: String,
    val konten: String,
    val gambar_url: String? = null,      // URL gambar
    val author_id: String? = null,       // foreign key ke users
    val created_at: String? = null       // auto timestamp
)
