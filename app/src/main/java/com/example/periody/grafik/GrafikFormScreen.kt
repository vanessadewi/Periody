package com.example.periody.grafik

import android.graphics.BitmapFactory
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.periody.grafik.presentation.GrafikViewModel
import kotlinx.coroutines.launch
import com.example.periody.supabase.SupabaseProvider
import io.github.jan.supabase.gotrue.auth


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun GrafikFormScreen(
    viewModel: GrafikViewModel,
    navController: NavController
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val uiState by viewModel.state.collectAsState()

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var imageBytes by remember { mutableStateOf<ByteArray?>(null) }
    var isUploading by remember { mutableStateOf(false) }

    val session = SupabaseProvider.client.auth.currentSessionOrNull()
    val userId = session?.user?.id

    if (userId == null) {
        Text("Anda belum login. Silakan login terlebih dahulu.", color = MaterialTheme.colorScheme.error)
        return
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            isUploading = true
            val inputStream = context.contentResolver.openInputStream(it)
            imageBytes = inputStream?.readBytes()
            isUploading = false
        }
    }

    Column(Modifier.padding(16.dp)) {
        Text("Tambah Grafik", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Judul") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Deskripsi") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        Button(onClick = { launcher.launch("image/*") }) {
            Text("Pilih Gambar")
        }

        if (isUploading) {
            Spacer(Modifier.height(8.dp))
            Text("Mengunggah gambar...", style = MaterialTheme.typography.bodySmall)
        }

        imageBytes?.let {
            val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "Preview Gambar",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            )
            Spacer(Modifier.height(12.dp))
        }

        uiState.error?.let {
            Text("Gagal menyimpan: $it", color = MaterialTheme.colorScheme.error)
            Spacer(Modifier.height(8.dp))
        }

        Button(
            onClick = {
                imageBytes?.let {
                    println("userId dikirim ke grafik: $userId") // 🔍 Debug log
                    scope.launch {
                        viewModel.tambahGrafik(
                            title = title,
                            description = description.ifBlank { null },
                            imageBytes = it
                        ) {
                            title = ""
                            description = ""
                            imageBytes = null
                            navController.popBackStack()
                        }
                    }
                }
            },
            enabled = imageBytes != null && title.isNotBlank()
        ) {
            Text("Simpan")
        }
    }
}
