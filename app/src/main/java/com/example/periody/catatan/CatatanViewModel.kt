package com.example.periody.catatan

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.periody.model.Catatan
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.UUID

class CatatanViewModel(
    private val repo: CatatanRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CatatanState())
    val state: StateFlow<CatatanState> = _state

    // ⭐ INI YANG DIPAKAI HomeScreen
    val list: StateFlow<List<Catatan>> =
        state.map { it.list }
            .let { mapped -> MutableStateFlow(emptyList<Catatan>()) }
            .also { flow ->
                viewModelScope.launch {
                    state.collect { flow.value = it.list }
                }
            }

    fun loadAll(userId: String) {
        viewModelScope.launch {
            try {
                _state.value = _state.value.copy(loading = true)
                val data = repo.getAll(userId)
                _state.value = _state.value.copy(
                    list = data,
                    loading = false
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    loading = false,
                    error = e.message
                )
            }
        }
    }

    fun loadDetail(id: String) {
        viewModelScope.launch {
            try {
                _state.value = _state.value.copy(loading = true)
                val catatan = repo.getById(id)
                _state.value = _state.value.copy(
                    selected = catatan,
                    loading = false
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    loading = false,
                    error = e.message
                )
            }
        }
    }

    fun tambah(
        context: Context,
        userId: String,
        tanggal: String,
        gejala: String?,
        intensitas: Double?,
        gejalaTambahan: List<String>?,
        fotoUri: Uri?,
        onDone: () -> Unit
    ) {
        viewModelScope.launch {
            try {
                val storage = CatatanStorageHelper(context)
                val fotoUrl = fotoUri?.let { storage.uploadFoto(it, userId) }

                val catatan = Catatan(
                    id = UUID.randomUUID().toString(),
                    user_id = userId,
                    tanggal = tanggal,
                    gejala = gejala,
                    intensitas = intensitas,
                    gejala_tambahan = gejalaTambahan,
                    foto_url = fotoUrl
                )

                repo.insert(catatan)
                loadAll(userId)
                onDone()

            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message)
            }
        }
    }

    fun update(
        context: Context,
        existing: Catatan,
        tanggal: String,
        gejala: String?,
        intensitas: Double?,
        gejalaTambahan: List<String>?,
        fotoUri: Uri?,
        onDone: () -> Unit
    ) {
        viewModelScope.launch {
            try {
                val storage = CatatanStorageHelper(context)
                val fotoUrl = fotoUri?.let { storage.uploadFoto(it, existing.user_id ?: "") }
                    ?: existing.foto_url

                val updated = existing.copy(
                    tanggal = tanggal,
                    gejala = gejala,
                    intensitas = intensitas,
                    gejala_tambahan = gejalaTambahan,
                    foto_url = fotoUrl
                )

                repo.update(updated)
                loadAll(existing.user_id ?: "")
                onDone()

            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message)
            }
        }
    }

    fun hapus(id: String, onDone: () -> Unit) {
        viewModelScope.launch {
            try {
                repo.delete(id)
                _state.value = _state.value.copy(
                    list = _state.value.list.filterNot { it.id == id }
                )
                onDone()
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message)
            }
        }
    }
}