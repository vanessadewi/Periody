package com.example.periody.tweet

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage

@Composable
fun TweetDetailScreen(
    navController: NavController,
    viewModel: TweetViewModel,
    id: String
) {
    val state by viewModel.state.collectAsState()
    val tweet = state.selected

    LaunchedEffect(id) {
        viewModel.loadDetail(id)
    }

    if (tweet == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Loading...")
        }
        return
    }

    Column(modifier = Modifier.padding(16.dp)) {

        Text(
            text = tweet.konten,
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(Modifier.height(12.dp))

        if (!tweet.gambar_url.isNullOrEmpty()) {
            AsyncImage(
                model = tweet.gambar_url,
                contentDescription = "Tweet Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
        }

        Spacer(Modifier.height(12.dp))

        Text(
            text = "Tanggal: ${tweet.created_at ?: "-"}",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = { navController.navigate("tweet_edit/${tweet.id ?: ""}") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Edit Tweet")
        }

        Spacer(Modifier.height(12.dp))

        OutlinedButton(
            onClick = {
                viewModel.delete(tweet.id ?: return@OutlinedButton) {
                    navController.popBackStack()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Hapus Tweet")
        }
    }
}
