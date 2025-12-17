package com.example.periody.catatan

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatatanDetailScreen(
    navController: NavController,
    viewModel: CatatanViewModel,
    id: String
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(id) {
        viewModel.loadDetail(id)
    }

    val catatan = state.selected ?: state.list.find { it.id == id }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail Catatan") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali"
                        )
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            navController.navigate("catatan_edit/$id")
                        }
                    ) {
                        Text("Edit")
                    }
                }
            )
        }
    ) { padding ->

        if (catatan == null) {
            Box(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                if (state.loading) {
                    CircularProgressIndicator()
                } else {
                    Text("Catatan tidak ditemukan")
                }
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = catatan.tanggal,
                style = MaterialTheme.typography.titleLarge
            )

            catatan.gejala?.let {
                Text(
                    text = "Gejala: $it",
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            catatan.intensitas?.let {
                Text(
                    text = "Intensitas: $it",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            if (!catatan.foto_url.isNullOrEmpty()) {
                Image(
                    painter = rememberAsyncImagePainter(catatan.foto_url),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    viewModel.hapus(id) {
                        navController.popBackStack()
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Hapus")
            }

            state.error?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}