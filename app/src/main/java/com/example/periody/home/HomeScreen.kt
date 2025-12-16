package com.example.periody.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.periody.artikel.ArtikelCardComposable
import com.example.periody.artikel.ArtikelViewModel
import com.example.periody.catatan.CatatanCard
import com.example.periody.catatan.CatatanViewModel
import com.example.periody.grafik.GrafikCard
import com.example.periody.grafik.GrafikViewModel
import com.example.periody.navigation.BottomBar
import com.example.periody.reminder.ReminderCard
import com.example.periody.reminder.ReminderViewModel
import com.example.periody.supabase.SupabaseProvider
import com.example.periody.tweet.TweetCard
import com.example.periody.tweet.TweetViewModel
import io.github.jan.supabase.gotrue.auth

@Composable
fun HomeScreen(
    navController: NavController,
    catatanViewModel: CatatanViewModel,
    grafikViewModel: GrafikViewModel,
    reminderViewModel: ReminderViewModel,
    artikelViewModel: ArtikelViewModel,
    tweetViewModel: TweetViewModel
) {
    val userId = SupabaseProvider.client.auth.currentUserOrNull()?.id

    if (userId == null) {
        Text("User belum login")
        return
    }

    // LOAD DATA
    LaunchedEffect(userId) {
        catatanViewModel.loadAll(userId)
        grafikViewModel.loadData(userId)   // ← FIX
        reminderViewModel.load(userId)
        artikelViewModel.loadAll()
        tweetViewModel.loadTweets(userId)
    }

    // STATE
    val catatanList by catatanViewModel.list.collectAsState()
    val catatanTerbaru = catatanList.take(3)

    val artikelState by artikelViewModel.state.collectAsState()
    val artikelTerbaru = artikelState.items.lastOrNull()

    val grafikState by grafikViewModel.state.collectAsState()

    val reminderState by reminderViewModel.state.collectAsState()
    val reminderTerdekat = reminderState.list.firstOrNull()

    val tweetState by tweetViewModel.state.collectAsState()
    val tweetTerbaru = tweetState.list.lastOrNull()

    Scaffold(
        bottomBar = { BottomBar(navController) }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(20.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {

            // HEADER
            item {
                Text(
                    text = "Halo!",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            // CATATAN
            item {
                Text("Catatan Terbaru", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }

            if (catatanTerbaru.isEmpty()) {
                item { Text("Belum ada catatan", color = MaterialTheme.colorScheme.onSurfaceVariant) }
            } else {
                items(catatanTerbaru) { catatan ->
                    CatatanCard(catatan = catatan) {
                        navController.navigate("catatan_detail/${catatan.id}")
                    }
                }
            }

            item {
                OutlinedButton(
                    onClick = { navController.navigate("catatan") },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Lihat Semua Catatan") }
            }

            item {
                Button(
                    onClick = { navController.navigate("catatan_form") },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Tambah Catatan") }
            }

            // GRAFIK
            item {
                GrafikCard(
                    grafik = grafikState,
                    onClick = { navController.navigate("grafik") }
                )
            }

            // REMINDER
            item {
                ReminderCard(
                    reminder = reminderTerdekat,
                    onClick = { navController.navigate("reminder") }
                )
            }

            // ARTIKEL
            if (artikelTerbaru != null) {
                item {
                    ArtikelCardComposable(
                        artikel = artikelTerbaru,
                        onClick = { navController.navigate("artikel_detail/${artikelTerbaru.id}") },
                        onEdit = { navController.navigate("artikel_edit/${artikelTerbaru.id}") },
                        onDelete = {
                            artikelViewModel.deleteArtikel(artikelTerbaru.id!!)
                            artikelViewModel.loadAll()
                        }
                    )
                }
            }

            // TWEET
            if (tweetTerbaru != null) {
                item {
                    TweetCard(
                        tweet = tweetTerbaru,
                        onClick = { navController.navigate("tweet") }
                    )
                }
            }
        }
    }
}
