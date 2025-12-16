package com.example.periody.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String,
    val email: String,
    val username: String? = null,
    @SerialName("first_name") val firstName: String? = null,
    @SerialName("last_name") val lastName: String? = null,
    val name: String? = null,
    val phone: String? = null,
    val address: String? = null,
    @SerialName("profile_image_url") val profileImageUrl: String? = null,

    // ✅ Tambahan untuk ProfileScreen
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null,
    @SerialName("email_confirmed_at") val emailConfirmedAt: String? = null
) {
    val emailVerified: Boolean
        get() = emailConfirmedAt != null
}
