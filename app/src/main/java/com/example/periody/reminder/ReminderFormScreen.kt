package com.example.periody.reminder

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import kotlinx.coroutines.launch

@Composable
fun ReminderFormScreen(
    navController: NavController,
    viewModel: ReminderViewModel = viewModel(),
    mode: String, // "tambah" atau "edit"
    id: String?,
    userId: String
) {
    val state by viewModel.state.collectAsState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var judul by remember { mutableStateOf("") }
    var deskripsi by remember { mutableStateOf("") }
    var waktu by remember { mutableStateOf("") }
    var iconUrl by remember { mutableStateOf<String?>(null) }
    var isUploading by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            isUploading = true
            val bytes = context.contentResolver.openInputStream(uri)?.readBytes()
            if (bytes != null) {
                scope.launch {
                    val url = ReminderStorageHelper.uploadIcon(bytes)
                    iconUrl = url
                    isUploading = false
                }
            } else {
                isUploading = false
            }
        }
    }

    LaunchedEffect(mode, id) {
        if (mode == "edit" && id != null) {
            viewModel.loadDetail(id)
        }
    }

    LaunchedEffect(state.selected) {
        if (mode == "edit" && state.selected != null) {
            val r = state.selected!!
            judul = r.judul
            deskripsi = r.deskripsi.orEmpty()
            waktu = r.waktu
            iconUrl = r.icon_url
        }
    }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {

            Text(
                text = if (mode == "edit") "Edit Reminder" else "Tambah Reminder",
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = judul,
                onValueChange = { judul = it },
                label = { Text("Judul") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = deskripsi,
                onValueChange = { deskripsi = it },
                label = { Text("Deskripsi") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = waktu,
                onValueChange = { waktu = it },
                label = { Text("Waktu (contoh: 08:00)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            Text("Icon Reminder", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))

            if (isUploading) {
                CircularProgressIndicator()
            } else {
                Button(
                    onClick = { launcher.launch("image/*") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (iconUrl == null) "Upload Icon" else "Ganti Icon")
                }
            }

            iconUrl?.let {
                Spacer(Modifier.height(8.dp))
                AsyncImage(
                    model = it,
                    contentDescription = "Preview Icon",
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
            }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    if (mode == "edit" && id != null) {
                        viewModel.update(
                            id = id,
                            judul = judul,
                            deskripsi = deskripsi.ifBlank { null },
                            waktu = waktu,
                            iconUrl = iconUrl
                        ) {
                            navController.popBackStack()
                        }
                    } else {
                        viewModel.tambah(
                            userId = userId,
                            judul = judul,
                            deskripsi = deskripsi.ifBlank { null },
                            waktu = waktu,
                            iconUrl = iconUrl
                        ) {
                            navController.popBackStack()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (mode == "edit") "Simpan Perubahan" else "Simpan")
            }

            state.error?.let {
                Spacer(Modifier.height(8.dp))
                Text(it, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}
