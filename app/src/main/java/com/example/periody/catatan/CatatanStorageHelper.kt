package com.example.periody.catatan

import android.content.Context
import android.net.Uri
import com.example.periody.supabase.SupabaseProvider
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID

class CatatanStorageHelper(
    private val context: Context
) {

    private val client = SupabaseProvider.client
    private val bucket = client.storage["catatan"]

    suspend fun uploadFoto(uri: Uri, userId: String): String {
        return withContext(Dispatchers.IO) {

            val inputStream = context.contentResolver.openInputStream(uri)
                ?: throw IllegalStateException("Tidak bisa membuka foto")

            val bytes = inputStream.readBytes()
            inputStream.close()

            val fileName = "catatan_${userId}_${UUID.randomUUID()}.jpg"

            bucket.upload(
                path = fileName,
                data = bytes,
                upsert = false
            )

            bucket.publicUrl(fileName)
        }
    }
}
