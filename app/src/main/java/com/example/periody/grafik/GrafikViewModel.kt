package com.example.periody.grafik.presentation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.periody.grafik.data.GrafikRepository
import com.example.periody.grafik.data.GrafikStorageHelper
import com.example.periody.model.Grafik
import com.example.periody.supabase.SupabaseProvider
import io.github.jan.supabase.gotrue.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.UUID

class GrafikViewModel(
    private val repository: GrafikRepository = GrafikRepository()
) : ViewModel() {

    private val _state = MutableStateFlow(GrafikState())
    val state: StateFlow<GrafikState> = _state

    fun loadData() {
        viewModelScope.launch {
            val userId = SupabaseProvider.client.auth.currentSessionOrNull()?.user?.id
            if (userId == null) {
                _state.value = _state.value.copy(error = "Anda belum login.")
                return@launch
            }

            _state.value = _state.value.copy(isLoading = true)
            try {
                val list = repository.getGrafikByUser(userId)
                _state.value = _state.value.copy(grafikList = list, isLoading = false)
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message, isLoading = false)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun tambahGrafik(
        title: String,
        description: String?,
        imageBytes: ByteArray,
        onDone: () -> Unit
    ) {
        viewModelScope.launch {
            val userId = SupabaseProvider.client.auth.currentSessionOrNull()?.user?.id
            if (userId == null) {
                _state.value = _state.value.copy(error = "Anda belum login.")
                return@launch
            }

            try {
                println("📤 Uploading image for userId: $userId")
                val imageUrl = GrafikStorageHelper.uploadImage(imageBytes)
                println("✅ Image uploaded: $imageUrl")

                val grafik = Grafik(
                    id = UUID.randomUUID().toString(),
                    title = title,
                    description = description,
                    image_url = imageUrl,
                    created_at = Instant.now().toString(),
                    user_id = userId
                )

                println("📝 Inserting grafik: $grafik")
                repository.insertGrafik(grafik)

                loadData()
                onDone()
            } catch (e: Exception) {
                println("❌ Error saat tambahGrafik: ${e.message}")
                _state.value = _state.value.copy(error = e.message)
            }
        }
    }

    fun hapusGrafik(id: String) {
        viewModelScope.launch {
            val userId = SupabaseProvider.client.auth.currentSessionOrNull()?.user?.id
            if (userId == null) {
                _state.value = _state.value.copy(error = "Anda belum login.")
                return@launch
            }

            try {
                println("🗑️ Menghapus grafik id: $id untuk userId: $userId")
                repository.deleteGrafik(id)
                loadData()
            } catch (e: Exception) {
                println("❌ Error saat hapusGrafik: ${e.message}")
                _state.value = _state.value.copy(error = e.message)
            }
        }
    }
}
