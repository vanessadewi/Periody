package com.example.periody.catatan

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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

    val existing = remember(state.list, state.selected, id) {
        state.list.find { it.id == id } ?: state.selected
    }

    var tanggal by remember { mutableStateOf(existing?.tanggal ?: "") }
    var intensitasInput by remember { mutableStateOf(existing?.intensitas?.toString() ?: "") }

    var gejala by remember { mutableStateOf(existing?.gejala ?: "") }
    var expanded by remember { mutableStateOf(false) }

    var gejalaTambahan by remember { mutableStateOf(existing?.gejala_tambahan ?: emptyList()) }
    val daftarGejalaTambahan = listOf("Demam", "Batuk", "Pusing", "Mual", "Sesak Napas", "Nyeri Otot")

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    val pickImage = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri -> selectedImageUri = uri }

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
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            OutlinedTextField(
                value = tanggal,
                onValueChange = { tanggal = it },
                label = { Text("Tanggal") },
                modifier = Modifier.fillMaxWidth()
            )

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = gejala,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Gejala Utama") },
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

            Text("Gejala Tambahan", style = MaterialTheme.typography.titleMedium)

            daftarGejalaTambahan.forEach { item ->
                Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                    Checkbox(
                        checked = gejalaTambahan.contains(item),
                        onCheckedChange = {
                            gejalaTambahan = if (gejalaTambahan.contains(item)) {
                                gejalaTambahan - item
                            } else {
                                gejalaTambahan + item
                            }
                        }
                    )
                    Text(item)
                }
            }

            OutlinedTextField(
                value = intensitasInput,
                onValueChange = { intensitasInput = it },
                label = { Text("Intensitas (0.0 - 10.0)") },
                modifier = Modifier.fillMaxWidth()
            )

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

            state.error?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}
