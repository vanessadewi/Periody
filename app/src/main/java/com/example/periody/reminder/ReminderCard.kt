package com.example.periody.reminder

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.periody.model.Reminder

@Composable
fun ReminderCard(
    reminder: Reminder?,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("Reminder", fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))

            if (reminder == null) {
                Text("Belum ada reminder")
            } else {
                Text(reminder.judul, style = MaterialTheme.typography.titleMedium)
                if (!reminder.deskripsi.isNullOrEmpty()) {
                    Spacer(Modifier.height(4.dp))
                    Text(reminder.deskripsi ?: "")
                }
                if (reminder.waktu.isNotEmpty()) {
                    Spacer(Modifier.height(4.dp))
                    Text("Waktu: ${reminder.waktu}")
                }
                if (!reminder.icon_url.isNullOrEmpty()) {
                    AsyncImage(
                        model = reminder.icon_url,
                        contentDescription = "Reminder Icon",
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                    Spacer(Modifier.height(8.dp))
                }

            }
        }
    }
}
