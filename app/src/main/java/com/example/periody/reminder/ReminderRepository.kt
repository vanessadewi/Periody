package com.example.periody.reminder

import com.example.periody.model.Reminder
import com.example.periody.supabase.SupabaseProvider
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order

class ReminderRepository {

    private val client = SupabaseProvider.client

    suspend fun getByUser(userId: String): List<Reminder> {
        return client.from("reminder")
            .select {
                filter {
                    eq("user_id", userId)
                }
                order(
                    column = "waktu",
                    order = Order.ASCENDING
                )
            }
            .decodeList<Reminder>()
    }

    suspend fun getById(id: String): Reminder? {
        val list = client.from("reminder")
            .select {
                filter {
                    eq("id", id)
                }
                limit(1)
            }
            .decodeList<Reminder>()

        return list.firstOrNull()
    }

    suspend fun insert(reminder: Reminder): Reminder {
        return client.from("reminder")
            .insert(reminder) {
                select()
            }
            .decodeSingle<Reminder>()
    }

    suspend fun update(reminder: Reminder): Reminder {
        return client.from("reminder")
            .update(reminder) {
                filter {
                    eq("id", reminder.id)
                }
                select()
            }
            .decodeSingle<Reminder>()
    }

    suspend fun delete(id: String) {
        client.from("reminder")
            .delete {
                filter {
                    eq("id", id)
                }
            }
    }
}
