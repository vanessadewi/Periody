package com.example.periody.profile

import com.example.periody.supabase.SupabaseProvider
import io.github.jan.supabase.postgrest.from

suspend fun updateProfileRemote(
    userId: String,
    username: String?,
    name: String?,
    phone: String?,
    address: String?
) {
    val supabase = SupabaseProvider.client

    supabase.from("users").update(
        mapOf(
            "username" to username,
            "name" to name,
            "phone" to phone,
            "address" to address
        )
    ) {
        filter { eq("id", userId) }
    }
}
