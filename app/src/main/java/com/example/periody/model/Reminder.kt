package com.example.periody.model

import kotlinx.serialization.Serializable

@Serializable
data class Reminder(
    val id: String = "",
    val user_id: String? = null,
    val judul: String = "",
    val deskripsi: String? = null,
    val waktu: String = "",
    val icon_url: String? = null,
    val created_at: String? = null
)
