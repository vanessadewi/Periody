package com.example.periody.tweet

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
fun TweetFormScreen(
    navController: NavController,
    viewModel: TweetViewModel = viewModel(),
    mode: String,
    id: String?,
    userId: String
) {
    val state by viewModel.state.collectAsState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var konten by remember { mutableStateOf("") }
    var gambarUrl by remember { mutableStateOf<String?>(null) }
    var isUploading by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            isUploading = true
            val bytes = context.contentResolver.openInputStream(uri)?.readBytes()
            if (bytes != null) {
                scope.launch {
                    val url = TweetStorageHelper.uploadImage(bytes)
                    gambarUrl = url
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
            val t = state.selected!!
            konten = t.konten
            gambarUrl = t.gambar_url
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
                text = if (mode == "edit") "Edit Tweet" else "Buat Tweet",
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = konten,
                onValueChange = { konten = it },
                label = { Text("Apa yang kamu pikirkan?") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            if (isUploading) {
                CircularProgressIndicator()
            } else {
                Button(
                    onClick = { launcher.launch("image/*") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (gambarUrl == null) "Upload Gambar" else "Ganti Gambar")
                }
            }

            gambarUrl?.let {
                Spacer(Modifier.height(8.dp))
                AsyncImage(
                    model = it,
                    contentDescription = "Preview Gambar",
                    modifier = Modifier
                        .size(120.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
            }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    if (mode == "edit" && id != null) {
                        viewModel.update(id, konten, gambarUrl) {
                            navController.popBackStack()
                        }
                    } else {
                        viewModel.tambah(userId, konten, gambarUrl) {
                            navController.popBackStack()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (mode == "edit") "Simpan Perubahan" else "Tweet")
            }

            state.error?.let {
                Spacer(Modifier.height(8.dp))
                Text(it, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}
