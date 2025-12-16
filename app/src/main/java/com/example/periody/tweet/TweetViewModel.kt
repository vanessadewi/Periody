package com.example.periody.tweet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.periody.model.Tweet
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TweetViewModel : ViewModel() {

    private val repo = TweetRepository()

    private val _state = MutableStateFlow(TweetState())
    val state: StateFlow<TweetState> = _state

    // ============================
    // LOAD LIST
    // ============================
    fun loadTweets(userId: String) {
        viewModelScope.launch {
            try {
                val data = repo.getAll(userId)
                _state.value = _state.value.copy(list = data)
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message)
            }
        }
    }

    // ============================
    // LOAD DETAIL
    // ============================
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

    // ============================
    // TAMBAH TWEET
    // ============================
    fun tambah(userId: String, konten: String, gambarUrl: String?, onDone: () -> Unit) {
        viewModelScope.launch {
            try {
                repo.insert(
                    Tweet(
                        user_id = userId,
                        konten = konten,
                        gambar_url = gambarUrl
                    )
                )
                onDone()
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message)
            }
        }
    }

    // ============================
    // UPDATE TWEET
    // ============================
    fun update(id: String, konten: String, gambarUrl: String?, onDone: () -> Unit) {
        viewModelScope.launch {
            try {
                repo.update(
                    id,
                    Tweet(
                        id = id,
                        user_id = state.value.selected?.user_id ?: "",
                        konten = konten,
                        gambar_url = gambarUrl
                    )
                )
                onDone()
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message)
            }
        }
    }

    // ============================
    // DELETE TWEET
    // ============================
    fun delete(id: String, onDone: () -> Unit) {
        viewModelScope.launch {
            try {
                repo.delete(id)
                onDone()
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message)
            }
        }
    }
}
