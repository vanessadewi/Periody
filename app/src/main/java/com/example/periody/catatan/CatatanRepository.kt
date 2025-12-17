package com.example.periody.catatan

import com.example.periody.model.Catatan
import com.example.periody.supabase.SupabaseProvider
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.result.PostgrestResult

class CatatanRepository {

    private val client = SupabaseProvider.client

    suspend fun getAll(userId: String): List<Catatan> {
        return client.from("catatan")
            .select {
                filter {
                    Catatan::user_id eq userId
                }
            }
            .decodeList<Catatan>()
    }

    suspend fun getById(id: String): Catatan? {
        val result: PostgrestResult = client.from("catatan")
            .select {
                filter {
                    Catatan::id eq id
                }
            }
        val list = result.decodeList<Catatan>()
        return list.firstOrNull()
    }

    suspend fun insert(catatan: Catatan) {
        client.from("catatan").insert(catatan)
    }

    suspend fun update(catatan: Catatan) {
        client.from("catatan").update(catatan) {
            filter {
                Catatan::id eq catatan.id
            }
        }
    }

    suspend fun delete(id: String) {
        client.from("catatan").delete {
            filter {
                Catatan::id eq id
            }
        }
    }
}