package com.example.periody.auth

import com.example.periody.model.User
import com.example.periody.supabase.SupabaseProvider
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepository {

    private val supabase = SupabaseProvider.client

    // ✅ REGISTER (Auth + Insert ke tabel users)
    suspend fun register(
        email: String,
        password: String,
        username: String,
        firstName: String?,
        lastName: String?
    ): String = withContext(Dispatchers.IO) {

        // ✅ Sign up dengan API baru
        supabase.auth.signUpWith(Email) {
            this.email = email
            this.password = password
        }

        val userId = supabase.auth.currentSessionOrNull()?.user?.id
            ?: throw Exception("User ID tidak ditemukan setelah registrasi")

        val fullName = listOfNotNull(firstName, lastName)
            .joinToString(" ")
            .ifBlank { null }

        // ✅ Insert ke tabel users
        supabase.from("users").insert(
            mapOf(
                "id" to userId,
                "email" to email,
                "username" to username,
                "first_name" to firstName,
                "last_name" to lastName,
                "name" to fullName,
                "phone" to null,
                "address" to null,
                "profile_image_url" to null
            )
        )

        userId
    }

    // ✅ LOGIN
    suspend fun login(email: String, password: String) = withContext(Dispatchers.IO) {
        supabase.auth.signInWith(Email) {
            this.email = email
            this.password = password
        }
    }

    // ✅ LOGOUT
    suspend fun logout() = withContext(Dispatchers.IO) {
        supabase.auth.signOut()
    }

    // ✅ GET CURRENT USER ID
    suspend fun getCurrentUserId(): String? = withContext(Dispatchers.IO) {
        supabase.auth.currentSessionOrNull()?.user?.id
    }

    // ✅ GET USER BY ID
    suspend fun getUserById(id: String): User = withContext(Dispatchers.IO) {
        supabase
            .from("users")
            .select {
                filter { eq("id", id) }
            }
            .decodeSingle()
    }
}