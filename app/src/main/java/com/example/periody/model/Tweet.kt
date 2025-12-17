package com.example.periody.model

import kotlinx.serialization.Serializable

@Serializable
data class Tweet(
    val id: String? = null,
    val user_id: String,
    val konten: String,
    val gambar_url: String? = null,
    val created_at: String? = null
)
