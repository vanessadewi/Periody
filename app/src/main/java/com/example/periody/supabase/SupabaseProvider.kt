package com.example.periody.supabase

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.storage.Storage


object SupabaseProvider {
    val client: SupabaseClient = createSupabaseClient(
        supabaseUrl = "https://hdimpkdfbckckfjuxzlp.supabase.co",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImhkaW1wa2RmYmNrY2tmanV4emxwIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjUyNjUwNjIsImV4cCI6MjA4MDg0MTA2Mn0.GIzUrewje2zqxbw-_aaJt-qXNlZOecYuLTrHaA6lUGw"
    ) {
        install(Postgrest)
        install(Auth)
        install(Storage)
        install(Realtime) }
}