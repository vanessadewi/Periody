package com.example.periody.catatan

import com.example.periody.model.Catatan

data class CatatanState(
    val list: List<Catatan> = emptyList(),
    val loading: Boolean = false,
    val error: String? = null,
    val selected: Catatan? = null
)