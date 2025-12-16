package com.example.periody.storage

import com.example.periody.supabase.SupabaseProvider
import io.github.jan.supabase.storage.storage
import java.util.UUID

class StorageRepository {

    private val supabase = SupabaseProvider.client

    suspend fun uploadCatatanPhoto(bytes: ByteArray): String {
        val fileName = "catatan_${UUID.randomUUID()}.jpg"

        supabase.storage.from("catatan-foto")
            .upload(fileName, bytes)

        return supabase.storage.from("catatan-foto")
            .publicUrl(fileName)
    }
}
