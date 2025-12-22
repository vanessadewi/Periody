package com.example.periody.grafik.presentation

import com.example.periody.model.Grafik
import kotlinx.serialization.Serializable

@Serializable
data class GrafikState(
    val grafikList: List<Grafik> = emptyList(),
    val gejalaUtama: Map<String, Int> = emptyMap(),
    val gejalaTambahan: Map<String, Int> = emptyMap(),
    val intensitasList: List<Double> = emptyList(),
    val rataRataIntensitas: Double? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
