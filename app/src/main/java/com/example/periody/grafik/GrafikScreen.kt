package com.example.periody.grafik.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.periody.auth.AuthViewModel
import com.example.periody.grafik.presentation.GrafikViewModel
import com.example.periody.grafik.ui.components.BarChart
import com.example.periody.grafik.ui.components.GrafikCard

@Composable
fun GrafikScreen(
    authViewModel: AuthViewModel,
    viewModel: GrafikViewModel,
    navController: NavController
) {
    val authState by authViewModel.state.collectAsState()
    val user = authState.currentUser
    val state by viewModel.state.collectAsState()

    LaunchedEffect(user?.id) {
        user?.id?.let { viewModel.loadData() }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        GrafikCard(
            title = "Grafik Jumlah Data",
            description = "Menampilkan jumlah grafik yang telah kamu buat."
        ) {
            BarChart(
                labels = listOf("Total"),
                values = listOf(state.grafikList.size)
            )
        }

        GrafikCard(
            title = "Judul Grafik Terbaru",
            description = "Menampilkan judul grafik terakhir yang kamu buat."
        ) {
            val latest = state.grafikList.firstOrNull()
            if (latest != null) {
                Text(text = latest.title, style = MaterialTheme.typography.bodyLarge)
            } else {
                Text("Belum ada grafik.")
            }
        }

        Button(
            onClick = { navController.navigate("grafik_form") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Tambah Grafik")
        }

        Button(
            onClick = { navController.navigate("grafik_list") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Lihat Semua Grafik")
        }

        state.error?.let {
            Text(it, color = MaterialTheme.colorScheme.error)
        }
    }
}
