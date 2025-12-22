package com.example.periody.grafik.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.periody.grafik.data.GrafikRepository
import com.example.periody.grafik.data.GrafikStorageHelper
import com.example.periody.model.Grafik
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

    fun loadData(userId: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                val list = repository.getGrafikByUser(userId)
                _state.value = _state.value.copy(grafikList = list, isLoading = false)
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message, isLoading = false)
            }
        }
    }

    fun tambahGrafik(
        userId: String,
        title: String,
        description: String?,
        imageBytes: ByteArray,
        onDone: () -> Unit
    ) {
        viewModelScope.launch {
            try {
                val imageUrl = GrafikStorageHelper.uploadImage(imageBytes)
                val grafik = Grafik(
                    id = UUID.randomUUID().toString(),
                    title = title,
                    description = description,
                    image_url = imageUrl,
                    created_at = Instant.now().toString(),
                    user_id = userId
                )
                repository.insertGrafik(grafik)
                loadData(userId)
                onDone()
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message)
            }
        }
    }

    fun hapusGrafik(id: String, userId: String) {
        viewModelScope.launch {
            try {
                repository.deleteGrafik(id)
                loadData(userId)
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message)
            }
        }
    }

}
