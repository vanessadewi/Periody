package com.example.periody.grafik

import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.periody.grafik.presentation.GrafikViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun GrafikFormScreen(
    userId: String,
    viewModel: GrafikViewModel,
    navController: NavController
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var imageBytes by remember { mutableStateOf<ByteArray?>(null) }
    var isUploading by remember { mutableStateOf(false) }

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

        Spacer(Modifier.height(12.dp))

        Button(
            onClick = {
                imageBytes?.let {
                    viewModel.tambahGrafik(
                        userId = userId,
                        title = title,
                        description = description.ifBlank { null },
                        imageBytes = it
                    ) {
                        navController.popBackStack()
                    }
                }
            },
            enabled = imageBytes != null && title.isNotBlank()
        ) {
            Text("Simpan")
        }
    }
}
