package com.example.periody.grafik.data

import com.example.periody.model.Grafik
import com.example.periody.supabase.SupabaseProvider
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order

class GrafikRepository {

    private val client = SupabaseProvider.client

    suspend fun insertGrafik(grafik: Grafik): Grafik {
        return client.from("grafik")
            .insert(grafik) {
                select()
            }
            .decodeSingle()
    }

    suspend fun getGrafikByUser(userId: String): List<Grafik> {
        return client.from("grafik")
            .select {
                filter { eq("user_id", userId) }
                order("created_at", Order.DESCENDING)
            }
            .decodeList()
    }

    suspend fun updateGrafik(grafik: Grafik): Grafik {
        return client.from("grafik")
            .update(grafik) {
                filter { eq("id", grafik.id) }
                select()
            }
            .decodeSingle()
    }

    suspend fun deleteGrafik(id: String) {
        client.from("grafik")
            .delete {
                filter { eq("id", id) }
            }
    }

}
