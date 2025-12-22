package com.example.periody.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.periody.model.*
import com.example.periody.grafik.presentation.GrafikState

@Composable
fun CatatanCard(catatan: Catatan?, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("Catatan Menstruasi", fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))

            if (catatan == null) {
                Text("Belum ada catatan terbaru")
            } else {
                Text("Tanggal: ${catatan.tanggal}")
                Text("Gejala: ${catatan.gejala ?: "-"}")
            }
        }
    }
}

@Composable
fun ReminderCard(reminder: Reminder?, onClick: () -> Unit) {
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
                Text(reminder.judul)
                Text(reminder.deskripsi ?: "")
            }
        }
    }
}

@Composable
fun GrafikCard(grafik: GrafikState?, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("Grafik Siklus", fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))

            if (grafik == null ||
                (grafik.gejalaUtama.isEmpty() &&
                        grafik.gejalaTambahan.isEmpty() &&
                        grafik.intensitasList.isEmpty())
            ) {
                Text("Belum ada data grafik")
            } else {
                val totalCatatan = grafik.intensitasList.size
                Text("Total Catatan: $totalCatatan")

                val gejalaTerbanyak = grafik.gejalaUtama.maxByOrNull { entry -> entry.value }?.key
                Text("Gejala Paling Sering: ${gejalaTerbanyak ?: "-"}")

                val intensitasAvg = grafik.rataRataIntensitas ?: 0.0
                Text("Rata-rata Intensitas: ${"%.1f".format(intensitasAvg)}")
            }
        }
    }
}

@Composable
fun ArtikelCard(artikel: Artikel?, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("Artikel Edukasi", fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))

            if (artikel == null) {
                Text("Belum ada artikel terbaru")
            } else {
                Text(artikel.judul)
            }
        }
    }
}

@Composable
fun TweetCard(tweet: Tweet?, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("Tweet", fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))

            if (tweet == null) {
                Text("Belum ada cuitan terbaru")
            } else {
                Text(tweet.konten)
            }
        }
    }
}
