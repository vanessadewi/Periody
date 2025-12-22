package com.example.periody.grafik.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.periody.grafik.presentation.GrafikViewModel
import com.example.periody.model.Grafik

@Composable
fun GrafikListScreen(
    userId: String,
    viewModel: GrafikViewModel,
    navController: NavController
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(userId) {
        viewModel.loadData(userId)
    }

    Column(Modifier.padding(16.dp)) {
        Text("Daftar Grafik", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(16.dp))

        if (state.isLoading) {
            CircularProgressIndicator()
        } else if (state.grafikList.isEmpty()) {
            Text("Belum ada grafik.")
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(state.grafikList) { grafik ->
                    GrafikItem(grafik, navController, viewModel)
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = { navController.navigate("grafik_form") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Tambah Grafik")
        }

        state.error?.let {
            Spacer(Modifier.height(8.dp))
            Text(it, color = MaterialTheme.colorScheme.error)
        }
    }
}

@Composable
fun GrafikItem(
    grafik: Grafik,
    navController: NavController,
    viewModel: GrafikViewModel
) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text(grafik.title, style = MaterialTheme.typography.titleMedium)
            grafik.description?.let {
                Spacer(Modifier.height(4.dp))
                Text(it)
            }
            Spacer(Modifier.height(8.dp))
            AsyncImage(
                model = grafik.image_url,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            )
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = {
                    navController.navigate("grafik_edit/${grafik.id}")
                }) {
                    Text("Edit")
                }
                Button(onClick = {
                    viewModel.hapusGrafik(grafik.id, grafik.user_id)
                }) {
                    Text("Hapus")
                }
            }
        }
    }
}
