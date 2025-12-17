package com.example.periody.model

import kotlinx.serialization.Serializable

@Serializable
data class Artikel(
    val id: String? = null,
    val judul: String,
    val konten: String,
    val gambar_url: String? = null,
    val author_id: String? = null,
    val created_at: String? = null
)
