package com.example.periody.grafik

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.periody.model.Catatan
import com.example.periody.supabase.SupabaseProvider
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class GrafikState(
    val gejalaUtama: Map<String, Int> = emptyMap(),
    val gejalaTambahan: Map<String, Int> = emptyMap(),
    val intensitasList: List<Double> = emptyList(),
    val rataRataIntensitas: Double? = null
)

class GrafikViewModel : ViewModel() {

    private val _state = MutableStateFlow(GrafikState())
    val state: StateFlow<GrafikState> = _state

    fun loadData(userId: String) {
        viewModelScope.launch {
            val data = SupabaseProvider.client
                .from("catatan")
                .select()
                .decodeList<Catatan>()

            val userData = data.filter { it.user_id == userId }

            val gejalaUtama = userData
                .mapNotNull { it.gejala }
                .groupingBy { it }
                .eachCount()

            val gejalaTambahan = userData
                .flatMap { it.gejala_tambahan ?: emptyList() }
                .groupingBy { it }
                .eachCount()

            val intensitasList = userData.mapNotNull { it.intensitas }
            val rataRata = intensitasList.takeIf { it.isNotEmpty() }?.average()

            _state.value = GrafikState(
                gejalaUtama = gejalaUtama,
                gejalaTambahan = gejalaTambahan,
                intensitasList = intensitasList,
                rataRataIntensitas = rataRata
            )
        }
    }
}
