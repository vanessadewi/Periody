package com.example.periody.artikel

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.periody.model.Artikel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtikelListScreen(
    navController: NavController,
    viewModel: ArtikelViewModel
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadAll()
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Daftar Artikel") })
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("artikel_form") }
            ) {
                Text("+")
            }
        }
    ) { padding ->

        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {

            when {
                state.loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                state.error != null -> {
                    Text(
                        text = state.error ?: "Terjadi kesalahan",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                state.items.isEmpty() -> {
                    Text(
                        text = "Belum ada artikel",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(state.items) { artikel ->
                            ArtikelCard(
                                artikel = artikel,
                                onOpenDetail = {
                                    navController.navigate("artikel_detail/${artikel.id}")
                                },
                                onEdit = {
                                    navController.navigate("artikel_edit/${artikel.id}")
                                },
                                onDelete = {
                                    artikel.id?.let { viewModel.deleteArtikel(it) }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ArtikelCard(
    artikel: Artikel,
    onOpenDetail: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpenDetail() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // Judul
            Text(
                text = artikel.judul,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(Modifier.height(8.dp))

            // Gambar
            if (!artikel.gambar_url.isNullOrEmpty()) {
                AsyncImage(
                    model = artikel.gambar_url,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                )

                Spacer(Modifier.height(8.dp))
            }

            // Konten singkat
            Text(
                text = artikel.konten.take(120) + "...",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(Modifier.height(12.dp))

            // Tombol Aksi
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {

                IconButton(
                    onClick = {
                        onEdit()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit"
                    )
                }

                IconButton(
                    onClick = {
                        onDelete()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Hapus",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}