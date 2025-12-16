package com.example.periody.model

import kotlinx.serialization.Serializable

@Serializable
data class Tweet(
    val id: String? = null,          // ← WAJIB nullable
    val user_id: String,             // user_id WAJIB ada
    val konten: String,
    val gambar_url: String? = null,  // boleh null
    val created_at: String? = null   // Supabase isi otomatis
)
