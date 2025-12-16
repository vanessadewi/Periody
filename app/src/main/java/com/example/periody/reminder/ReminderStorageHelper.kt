package com.example.periody.reminder

import com.example.periody.supabase.SupabaseProvider
import io.github.jan.supabase.storage.storage
import java.util.UUID

object ReminderStorageHelper {

    suspend fun uploadIcon(bytes: ByteArray): String {
        val bucket = SupabaseProvider.client.storage.from("reminder-icons")
        val fileName = "icon_${UUID.randomUUID()}.jpg"
        bucket.upload(fileName, bytes, upsert = false)
        return bucket.publicUrl(fileName)
    }
}
