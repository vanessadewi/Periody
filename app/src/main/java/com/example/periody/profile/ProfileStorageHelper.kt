package com.example.periody.profile

import android.content.Context
import android.net.Uri
import com.example.periody.supabase.SupabaseProvider
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID

class ProfileStorageHelper(private val context: Context) {

    private val client = SupabaseProvider.client
    private val bucket = client.storage["profile"]

    suspend fun uploadProfileImage(uri: Uri, userId: String): String {
        return withContext(Dispatchers.IO) {

            val inputStream = context.contentResolver.openInputStream(uri)
                ?: throw IllegalStateException("Tidak bisa membuka foto")

            val bytes = inputStream.readBytes()
            inputStream.close()

            val fileName = "profile_${userId}_${UUID.randomUUID()}.jpg"

            bucket.upload(
                path = fileName,
                data = bytes,
                upsert = true
            )

            bucket.publicUrl(fileName)
        }
    }
}
