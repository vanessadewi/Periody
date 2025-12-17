package com.example.periody.model

import kotlinx.serialization.Serializable

@Serializable
data class Catatan(
    val id: String,
    val user_id: String? = null,
    val tanggal: String,
    val gejala: String? = null,
    val foto_url: String? = null,
    val created_at: String? = null,
    val gejala_tambahan: List<String>? = null,
    val intensitas: Double? = null
)

