package com.example.periody.model

import kotlinx.serialization.Serializable

@Serializable
data class Grafik(
    val id: String,
    val title: String,
    val description: String?,
    val image_url: String,
    val created_at: String,
    val user_id: String
)
