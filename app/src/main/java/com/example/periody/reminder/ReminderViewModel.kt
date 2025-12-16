package com.example.periody.reminder

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.periody.model.Reminder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class ReminderState(
    val list: List<Reminder> = emptyList(),
    val selected: Reminder? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

class ReminderViewModel(
    private val repository: ReminderRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ReminderState())
    val state: StateFlow<ReminderState> = _state

    fun load(userId: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                val list = repository.getByUser(userId)
                _state.value = _state.value.copy(
                    list = list,
                    isLoading = false
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    fun loadDetail(id: String) {
        viewModelScope.launch {
            try {
                val reminder = repository.getById(id)
                _state.value = _state.value.copy(selected = reminder)
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message)
            }
        }
    }

    fun tambah(
        userId: String,
        judul: String,
        deskripsi: String?,
        waktu: String,
        iconUrl: String?,
        onDone: () -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                val reminder = Reminder(
                    user_id = userId,
                    judul = judul,
                    deskripsi = deskripsi,
                    waktu = waktu,
                    icon_url = iconUrl
                )
                val inserted = repository.insert(reminder)
                _state.value = _state.value.copy(
                    list = _state.value.list + inserted
                )
                onDone()
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message)
            }
        }
    }

    fun update(
        id: String,
        judul: String,
        deskripsi: String?,
        waktu: String,
        iconUrl: String?,
        onDone: () -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                val existing = _state.value.list.find { it.id == id } ?: return@launch
                val updatedReminder = existing.copy(
                    judul = judul,
                    deskripsi = deskripsi,
                    waktu = waktu,
                    icon_url = iconUrl
                )
                val updated = repository.update(updatedReminder)
                _state.value = _state.value.copy(
                    list = _state.value.list.map {
                        if (it.id == id) updated else it
                    },
                    selected = updated
                )
                onDone()
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message)
            }
        }
    }

    fun hapus(id: String, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                repository.delete(id)
                _state.value = _state.value.copy(
                    list = _state.value.list.filterNot { it.id == id },
                    selected = null
                )
                onDone()
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message)
            }
        }
    }
}
