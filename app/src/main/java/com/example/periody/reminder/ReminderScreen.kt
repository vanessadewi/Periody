package com.example.periody.reminder

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun ReminderDetailScreen(
    navController: NavController,
    viewModel: ReminderViewModel,
    id: String
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(id) {
        viewModel.loadDetail(id)
    }

    val reminder = state.selected

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            if (reminder == null) {
                Text("Memuat...")
                return@Column
            }

            Text(reminder.judul, style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(8.dp))

            if (!reminder.deskripsi.isNullOrEmpty()) {
                Text(reminder.deskripsi ?: "")
                Spacer(Modifier.height(8.dp))
            }

            if (reminder.waktu.isNotEmpty()) {
                Text("Waktu: ${reminder.waktu}")
                Spacer(Modifier.height(8.dp))
            }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    navController.navigate("reminder_edit/${reminder.id}")
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Edit")
            }

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = {
                    viewModel.hapus(reminder.id) {
                        navController.popBackStack()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.error)
            ) {
                Text("Hapus")
            }

            state.error?.let { err ->
                Spacer(Modifier.height(8.dp))
                Text(
                    text = err,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
