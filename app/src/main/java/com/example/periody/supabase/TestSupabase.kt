package com.example.periody.supabase

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import io.github.jan.supabase.postgrest.postgrest

object TestSupabase {

    fun testConnection() {
        CoroutineScope(Dispatchers.Main).launch {   // ✅ WAJIB MAIN THREAD
            try {
                val response = SupabaseProvider.client
                    .postgrest
                    .from("users")
                    .select {
                        limit(1)
                    }

                Log.d("SUPABASE_TEST", "✅ Koneksi berhasil: $response")
            } catch (e: Exception) {
                Log.e("SUPABASE_TEST", "❌ Koneksi gagal: ${e.message}")
            }
        }
    }
}
