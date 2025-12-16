package com.example.periody.artikel

import com.example.periody.model.Artikel
import com.example.periody.supabase.SupabaseProvider
import io.github.jan.supabase.postgrest.from

class ArtikelRepository {

    private val client = SupabaseProvider.client

    // GET ALL
    suspend fun getAll(): List<Artikel> {
        return client.from("artikel")
            .select()
            .decodeList<Artikel>()
    }

    // GET BY ID
    suspend fun getById(id: String): Artikel? {
        return client.from("artikel")
            .select {
                filter {
                    Artikel::id eq id
                }
            }
            .decodeSingleOrNull<Artikel>()
    }

    // INSERT
    suspend fun insert(artikel: Artikel) {
        client.from("artikel")
            .insert(artikel)
    }

    // UPDATE
    suspend fun update(id: String, artikel: Artikel) {
        client.from("artikel")
            .update(artikel) {
                filter {
                    Artikel::id eq id
                }
            }
    }

    // DELETE
    suspend fun delete(id: String) {
        client.from("artikel")
            .delete {
                filter {
                    Artikel::id eq id
                }
            }
    }
}
