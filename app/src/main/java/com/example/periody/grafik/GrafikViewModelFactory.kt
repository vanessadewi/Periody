package com.example.periody.grafik.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.periody.grafik.data.GrafikRepository

class GrafikViewModelFactory(
    private val repository: GrafikRepository = GrafikRepository()
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GrafikViewModel::class.java)) {
            return GrafikViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
