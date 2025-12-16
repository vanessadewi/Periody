package com.example.periody.catatan

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.periody.model.Catatan

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatatanFormScreen(
    navController: NavController,
    viewModel: CatatanViewModel,
    mode: String,
    id: String?,
    userId: String
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsState()

    // Data existing jika mode edit
    val existing = remember(state.list, state.selected, id) {
        state.list.find { it.id == id } ?: state.selected
    }

    // ============================
    // STATE FORM
    // ============================
    var tanggal by remember { mutableStateOf(existing?.tanggal ?: "") }
    var intensitasInput by remember { mutableStateOf(existing?.intensitas?.toString() ?: "") }
    var gejalaTambahan by remember { mutableStateOf(existing?.gejala_tambahan ?: emptyList()) }

    // FOTO
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    val pickImage = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri -> selectedImageUri = uri }

    // ============================
    // DROPDOWN GEJALA
    // ============================
    var expanded by remember { mutableStateOf(false) }
    var gejala by remember { mutableStateOf(existing?.gejala ?: "") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(if (mode == "edit") "Edit Catatan" else "Tambah Catatan")
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // ============================
            // INPUT TANGGAL
            // ============================
            OutlinedTextField(
                value = tanggal,
                onValueChange = { tanggal = it },
                label = { Text("Tanggal") },
                modifier = Modifier.fillMaxWidth()
            )

            // ============================
            // DROPDOWN GEJALA
            // ============================
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = gejala,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Gejala") },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    daftarGejala.forEach { item ->
                        DropdownMenuItem(
                            text = { Text(item) },
                            onClick = {
                                gejala = item
                                expanded = false
                            }
                        )
                    }
                }
            }

            // ============================
            // INPUT INTENSITAS
            // ============================
            OutlinedTextField(
                value = intensitasInput,
                onValueChange = { intensitasInput = it },
                label = { Text("Intensitas (0.0 - 10.0)") },
                modifier = Modifier.fillMaxWidth()
            )

            // ============================
            // FOTO
            // ============================
            Button(onClick = { pickImage.launch("image/*") }) {
                Text("Pilih Foto")
            }

            val previewUrl = selectedImageUri?.toString() ?: existing?.foto_url

            if (!previewUrl.isNullOrEmpty()) {
                Image(
                    painter = rememberAsyncImagePainter(previewUrl),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentScale = ContentScale.Crop
                )
            }

            // ============================
            // TOMBOL SIMPAN / UPDATE
            // ============================
            Button(
                onClick = {
                    val intensitas = intensitasInput.toDoubleOrNull()

                    if (mode == "edit" && existing != null) {
                        viewModel.update(
                            context = context,
                            existing = existing,
                            tanggal = tanggal,
                            gejala = gejala.ifBlank { null },
                            intensitas = intensitas,
                            gejalaTambahan = gejalaTambahan,
                            fotoUri = selectedImageUri
                        ) {
                            navController.popBackStack()
                        }
                    } else {
                        viewModel.tambah(
                            context = context,
                            userId = userId,
                            tanggal = tanggal,
                            gejala = gejala.ifBlank { null },
                            intensitas = intensitas,
                            gejalaTambahan = gejalaTambahan,
                            fotoUri = selectedImageUri
                        ) {
                            navController.popBackStack()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (mode == "edit") "Update" else "Simpan")
            }

            // ERROR
            state.error?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}
