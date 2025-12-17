package com.example.periody.grafik

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.periody.auth.AuthViewModel

@Composable
fun GrafikScreen(
    authViewModel: AuthViewModel,
    viewModel: GrafikViewModel
) {
    val authState by authViewModel.state.collectAsState()
    val user = authState.currentUser
    val state by viewModel.state.collectAsState()

    LaunchedEffect(user?.id) {
        if (user != null) {
            viewModel.loadData(user.id)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {

        GrafikCard(
            title = "Grafik Gejala Utama",
            description = "Menampilkan jumlah gejala utama."
        ) {
            if (state.gejalaUtama.isEmpty()) {
                Text("Tidak ada data")
            } else {
                BarChart(
                    labels = state.gejalaUtama.keys.toList(),
                    values = state.gejalaUtama.values.toList()
                )
            }
        }

        GrafikCard(
            title = "Grafik Gejala Tambahan",
            description = "Menampilkan frekuensi gejala tambahan yang tercatat."
        ) {
            if (state.gejalaTambahan.isEmpty()) {
                Text("Tidak ada data")
            } else {
                BarChart(
                    labels = state.gejalaTambahan.keys.toList(),
                    values = state.gejalaTambahan.values.toList()
                )
            }
        }

        GrafikCard(
            title = "Grafik Intensitas",
            description = "Menampilkan tingkat intensitas gejala dari setiap catatan."
        ) {
            if (state.intensitasList.isEmpty()) {
                Text("Tidak ada data")
            } else {
                BarChart(
                    labels = state.intensitasList.indices.map { "C${it + 1}" },
                    values = state.intensitasList.map { it.toInt() }
                )
                Spacer(Modifier.height(8.dp))
                Text("Rata-rata Intensitas: ${"%.1f".format(state.rataRataIntensitas ?: 0.0)}")
            }
        }
    }
}
