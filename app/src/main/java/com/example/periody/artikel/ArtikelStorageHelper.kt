package com.example.periody.artikel

import android.content.Context
import android.net.Uri
import com.example.periody.supabase.SupabaseProvider
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID

class ArtikelStorageHelper(private val context: Context) {

    private val client = SupabaseProvider.client
    private val bucket = client.storage["artikel"]

    suspend fun uploadImage(uri: Uri, authorId: String): String {
        return withContext(Dispatchers.IO) {

            val inputStream = context.contentResolver.openInputStream(uri)
                ?: throw IllegalStateException("Tidak bisa membuka gambar")

            val bytes = inputStream.readBytes()
            inputStream.close()

            val fileName = "artikel_${authorId}_${UUID.randomUUID()}.jpg"

            bucket.upload(
                path = fileName,
                data = bytes,
                upsert = false
            )

            bucket.publicUrl(fileName)
        }
    }
}
