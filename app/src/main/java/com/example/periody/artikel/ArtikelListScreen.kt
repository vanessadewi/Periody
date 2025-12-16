package com.example.periody.artikel

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
    val list = state.items
    val loading = state.loading
    val error = state.error

    LaunchedEffect(Unit) {
        viewModel.loadAll()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Daftar Artikel") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("artikel_form") }
            ) {
                Text("+")
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {

            if (loading) {
                CircularProgressIndicator()
                return@Column
            }

            if (error != null) {
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error
                )
            }

            if (list.isEmpty()) {
                Text("Belum ada artikel")
                return@Column
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(list) { artikel ->
                    ArtikelCard(
                        artikel = artikel,
                        onClick = {
                            navController.navigate("artikel_detail/${artikel.id}")
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ArtikelCard(
    artikel: Artikel,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Text(
                artikel.judul,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(Modifier.height(8.dp))

            if (!artikel.gambar_url.isNullOrEmpty()) {
                AsyncImage(
                    model = artikel.gambar_url,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                )
            }

            Spacer(Modifier.height(8.dp))

            Text(
                artikel.konten.take(120) + "...",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
