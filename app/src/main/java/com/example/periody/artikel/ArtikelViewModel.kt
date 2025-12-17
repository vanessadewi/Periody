package com.example.periody.artikel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.periody.model.Artikel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class ArtikelState(
    val items: List<Artikel> = emptyList(),
    val selected: Artikel? = null,
    val loading: Boolean = false,
    val error: String? = null
)

class ArtikelViewModel(
    private val repo: ArtikelRepository = ArtikelRepository()
) : ViewModel() {

    private val _state = MutableStateFlow(ArtikelState())
    val state: StateFlow<ArtikelState> = _state

    fun loadAll() {
        viewModelScope.launch {
            try {
                val data = repo.getAll()
                _state.value = _state.value.copy(items = data)
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message)
            }
        }
    }

    fun loadDetail(id: String) {
        viewModelScope.launch {
            try {
                val data = repo.getById(id)
                _state.value = _state.value.copy(selected = data)
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message)
            }
        }
    }

    fun tambah(
        context: Context,
        authorId: String,
        judul: String,
        konten: String,
        gambarUri: Uri?,
        onDone: () -> Unit
    ) {
        viewModelScope.launch {
            try {
                val storage = ArtikelStorageHelper(context)

                val url = if (gambarUri != null) {
                    storage.uploadImage(gambarUri, authorId)
                } else null

                repo.insert(
                    Artikel(
                        judul = judul,
                        konten = konten,
                        gambar_url = url,
                        author_id = authorId
                    )
                )

                onDone()

            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message)
            }
        }
    }

    fun update(
        context: Context,
        artikel: Artikel,
        judul: String,
        konten: String,
        gambarUri: Uri?,
        onDone: () -> Unit
    ) {
        viewModelScope.launch {
            try {
                val storage = ArtikelStorageHelper(context)

                val url = if (gambarUri != null) {
                    storage.uploadImage(gambarUri, artikel.author_id ?: "")
                } else artikel.gambar_url

                repo.update(
                    artikel.id!!,
                    Artikel(
                        id = artikel.id,
                        judul = judul,
                        konten = konten,
                        gambar_url = url,
                        author_id = artikel.author_id,
                        created_at = artikel.created_at
                    )
                )

                onDone()

            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message)
            }
        }
    }

    fun deleteArtikel(id: String) {
        viewModelScope.launch {
            try {
                repo.delete(id)
                loadAll()
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message)
            }
        }
    }

}
