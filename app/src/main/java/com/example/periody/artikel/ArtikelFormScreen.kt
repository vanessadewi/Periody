package com.example.periody.artikel

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtikelFormScreen(
    navController: NavController,
    viewModel: ArtikelViewModel,
    authorId: String,
    existingId: String? = null
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsState()

    // Ambil artikel existing (kalau edit)
    val existing = state.items.find { it.id == existingId }

    var judul by remember { mutableStateOf(existing?.judul ?: "") }
    var konten by remember { mutableStateOf(existing?.konten ?: "") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val pickImage = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        selectedImageUri = uri
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (existing == null) "Tambah Artikel"
                        else "Edit Artikel"
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali"
                        )
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            OutlinedTextField(
                value = judul,
                onValueChange = { judul = it },
                label = { Text("Judul") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = konten,
                onValueChange = { konten = it },
                label = { Text("Konten") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            )

            Button(
                onClick = { pickImage.launch("image/*") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Pilih Gambar")
            }

            val preview = selectedImageUri?.toString() ?: existing?.gambar_url
            if (!preview.isNullOrEmpty()) {
                Image(
                    painter = rememberAsyncImagePainter(preview),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentScale = ContentScale.Crop
                )
            }

            Button(
                onClick = {
                    if (existing == null) {
                        // CREATE
                        viewModel.tambah(
                            context = context,
                            authorId = authorId,
                            judul = judul,
                            konten = konten,
                            gambarUri = selectedImageUri
                        ) {
                            navController.popBackStack()
                        }
                    } else {
                        // UPDATE
                        viewModel.update(
                            context = context,
                            artikel = existing,
                            judul = judul,
                            konten = konten,
                            gambarUri = selectedImageUri
                        ) {
                            navController.popBackStack()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (existing == null) "Simpan" else "Update")
            }
        }
    }
}