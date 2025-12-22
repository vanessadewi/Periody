package com.example.periody.grafik.data

import com.example.periody.supabase.SupabaseProvider
import io.github.jan.supabase.storage.storage
import java.util.UUID

object GrafikStorageHelper {

    suspend fun uploadImage(bytes: ByteArray): String {
        val fileName = "grafik/${UUID.randomUUID()}.jpg"
        val bucket = SupabaseProvider.client.storage.from("grafik-images")
        bucket.upload(fileName, bytes, upsert = true)
        return bucket.publicUrl(fileName)
    }
}
