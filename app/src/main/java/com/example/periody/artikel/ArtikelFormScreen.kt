package com.example.periody.artikel

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.periody.model.Artikel

@Composable
fun ArtikelFormScreen(
    navController: NavController,
    viewModel: ArtikelViewModel,
    authorId: String,
    existingId: String? = null
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsState()

    // Ambil artikel existing dari ViewModel
    val existing = state.items.find { it.id == existingId }

    var judul by remember { mutableStateOf(existing?.judul ?: "") }
    var konten by remember { mutableStateOf(existing?.konten ?: "") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val pickImage = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        selectedImageUri = uri
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
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
            modifier = Modifier.fillMaxWidth()
        )

        Button(onClick = { pickImage.launch("image/*") }) {
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
                    // TAMBAH ARTIKEL
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
                    // UPDATE ARTIKEL
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
