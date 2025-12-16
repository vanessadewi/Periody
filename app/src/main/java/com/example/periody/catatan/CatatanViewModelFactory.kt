package com.example.periody.catatan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class CatatanViewModelFactory(
    private val repo: CatatanRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CatatanViewModel::class.java)) {
            return CatatanViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
